package Server.gson;

import Server.gson.entities.War;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class GsonReader implements JsonReaderFacade {
    @Override
    public War readJson() {
        File file = new File(".\\src\\Server\\gson\\properties.json");
        try {
            FileReader fr = new FileReader(file);
            Gson gson = new Gson();
            War war = gson.fromJson(fr, War.class);

            return war;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
