package businesslogic.recipe;

import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CookingPreparation extends CookingProcedure {
    private int id;
    private String name;

    private static Map<Integer, CookingPreparation> cookingPreparations = new HashMap<>();

    public CookingPreparation(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static CookingPreparation loadPreparationById(int id) {
        if (cookingPreparations.containsKey(id))
            return cookingPreparations.get(id);
        String getPreparation = "SELECT * FROM CookingPreparation WHERE id = " + id + ";";
        PersistenceManager.executeQuery(getPreparation, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                cookingPreparations.put(id, new CookingPreparation(id, name));
            }
        });
        return cookingPreparations.get(id);
    }

    @Override
    public String getName() {
        return name;
    }

}
