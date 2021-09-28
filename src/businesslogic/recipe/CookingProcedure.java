package businesslogic.recipe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CookingProcedure {
    private int procedure_id;
    private List<CookingPreparation> preparations = new ArrayList<>();
    private String tacc;
    private String tacv;
    private String tuv;
    private String tuc;
    private String tt;

    private static Map<Integer, CookingProcedure> cookingProcedureMap = new HashMap<>();
    private static ObservableList<CookingProcedure> cookingProcedureObservableList = FXCollections.observableArrayList();

    public static CookingProcedure loadProcedureById(int id) {
        if (cookingProcedureMap.containsKey(id))
            return cookingProcedureMap.get(id);
        String getProcedure = "SELECT * FROM CookingProcedure where id = " + id + ";";
        PersistenceManager.executeQuery(getProcedure, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int is = rs.getInt("is_a");
                String type = rs.getString("type");
                CookingProcedure cookingProcedure;
                if (type.charAt(0) == 'r') {
                    Recipe recipe = Recipe.loadRecipeById(is);
                    if (recipe == null)
                        throw new SQLException("Recipe with id: " + is + " cannot be found, CookingProcedure id = " + id);
                    cookingProcedure = recipe;
                    recipe.setProcedure_id(id);
                    settingCookingProcedureTime(cookingProcedure, rs);
                    cookingProcedureMap.put(id, recipe);
                }
                else {
                    CookingPreparation cookingPreparation = CookingPreparation.loadPreparationById(is);
                    if (cookingPreparation == null)
                        throw new SQLException("CookingPreparation with id: " + is + " cannot be found, CookingProcedure id = " + id);
                    cookingProcedure = cookingPreparation;
                    cookingPreparation.setProcedure_id(id);
                    settingCookingProcedureTime(cookingProcedure, rs);
                    cookingProcedureMap.put(id, cookingPreparation);
                }
                String getPreparations = "SELECT * FROM Preparations WHERE procedure_id = " + id + ";";
                PersistenceManager.executeQuery(getPreparations, new ResultHandler() {
                    @Override
                    public void handle(ResultSet rs) throws SQLException {
                        cookingProcedure.preparations.add((CookingPreparation) loadProcedureById(rs.getInt("preparation_id")));
                    }
                });
            }
        });
        return cookingProcedureMap.get(id);
    }

    private static void settingCookingProcedureTime(CookingProcedure cookingProcedure, ResultSet rs) throws SQLException {
        cookingProcedure.tacc = rs.getString("tacc");
        cookingProcedure.tacv = rs.getString("tacv");
        cookingProcedure.tuc = rs.getString("tuc");
        cookingProcedure.tuv = rs.getString("tuv");
        cookingProcedure.tt = rs.getString("tt");
    }

    public static ObservableList<CookingProcedure> loadAllCookingProcedures() {
        /*if (cookingProcedureObservableList.size() > 0)
            return cookingProcedureObservableList;*/
        cookingProcedureObservableList.clear();
        String getProcedures = "SELECT id FROM CookingProcedure;";
        PersistenceManager.executeQuery(getProcedures, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                cookingProcedureObservableList.add(loadProcedureById(rs.getInt("id")));
            }
        });
        return  cookingProcedureObservableList;
    }

    public static CookingProcedure loadProcedureByType(int id, String type) {
        String getProcedureId = "SELECT * FROM CookingProcedure WHERE is_a = " + id + " and type = '" + type + "';";
        List<CookingProcedure> cookingProcedure = new ArrayList<>();
        PersistenceManager.executeQuery(getProcedureId, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                cookingProcedure.add(loadProcedureById(rs.getInt("id")));
            }
        });
        return cookingProcedure.get(0);
    }

    public static void deleteMenuProcedureReference(int menuId, int procedureId) {
        String deleteMenuProcedure = "SELECT * FROM MenuProcedures WHERE menu_id = " + menuId +
                " and procedure_id = " + procedureId + ";";
        PersistenceManager.executeUpdate(deleteMenuProcedure);
    }

    public static void saveMenuProcedure(int menuId, int cookingProcedureId) {
        String updateMenuProcedures = "INSERT INTO catering.MenuProcedures (menu_id, procedure_id) VALUES (" +
                menuId + ", " + cookingProcedureId + ");";
        PersistenceManager.executeUpdate(updateMenuProcedures);
    }

    public abstract String getName();

    public void setProcedure_id(int procedure_id) {
        this.procedure_id = procedure_id;
    }

    public int getProcedure_id() {
        return procedure_id;
    }

    //Recipe and CookingPreparation class should be inherit this method and used by Collections for verifying purposes
    @Override
    public boolean equals(Object obj) {
        if (obj != null && (obj.getClass() == Recipe.class || obj.getClass() == CookingPreparation.class)) {
            CookingProcedure cookingProcedure = (CookingProcedure) obj;
            if (procedure_id == cookingProcedure.getProcedure_id())
                return true;
        }
        return false;
    }

    public List<CookingPreparation> getPreparations() {
        return preparations;
    }

    public String getTacc() {
        return tacc;
    }

    public String getTacv() {
        return tacv;
    }

    public String getTuv() {
        return tuv;
    }

    public String getTuc() {
        return tuc;
    }

    public String getTt() {
        return tt;
    }

}
