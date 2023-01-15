package ausfin.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

@Service
public class TablesService {
    private ArrayList<ArrayList<Float>> taxTable;
    private ArrayList<ArrayList<Float>> helpTable;
    private ArrayList<ArrayList<Float>> mlsTable;

    public TablesService() {
        // Assumes json files in /resources/static/
        taxTable = jsonFileToTwoDFloatArr("tax.json");
        helpTable = jsonFileToTwoDFloatArr("help.json");
        mlsTable = jsonFileToTwoDFloatArr("mls.json");
    }

    private ArrayList<ArrayList<Float>> jsonFileToTwoDFloatArr(String fileName) {
        try {
            Gson gson = new GsonBuilder().create();
            Type listType = new TypeToken<ArrayList<ArrayList<Float>>>(){}.getType();
            return gson.fromJson(new InputStreamReader(getClass().getResourceAsStream("/static/".concat(fileName))), listType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<ArrayList<Float>> getTaxTable() {
        return taxTable;
    }

    public ArrayList<ArrayList<Float>> getHelpTable() {
        return helpTable;
    }

    public ArrayList<ArrayList<Float>> getMlsTable() {
        return mlsTable;
    }
}
