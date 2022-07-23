import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        // first task:
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCsv(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");

        // second task:
        List<Employee> list2 = parseXml("data.xml");
        String json2 = listToJson(list2);
        writeString(json2, "data2.json");

    }

    public static List<Employee> parseCsv(String[] columnNames, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnNames);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> staff = csv.parse();
            return staff;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeString(String string, String sourceName) {
        try (FileWriter file = new
                FileWriter(sourceName)) {
            file.write(string);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String listToJson(List<Employee> employees) {
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.toJson(employees, listType);
    }

    private static void read(Node node, List<Employee> employees) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                if (node_.getNodeName() == "employee") {
                    Element element = (Element) node_;
                    Long id = Long.parseLong(element.getElementsByTagName( "id").item(0).getTextContent());
                    String firstName = element.getElementsByTagName( "firstName").item(0).getTextContent();
                    String lastName = element.getElementsByTagName( "lastName").item(0).getTextContent();
                    String country = element.getElementsByTagName( "country").item(0).getTextContent();
                    int age = Integer.parseInt(element.getElementsByTagName( "age").item(0).getTextContent());
                    employees.add(new Employee(id, firstName, lastName, country, age));
                }
            }
        }
    }

    public static List<Employee> parseXml(String filename) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> employees = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse( new File(filename));
        Node root = doc.getDocumentElement();
        read(root, employees);
        return employees;
    }

}