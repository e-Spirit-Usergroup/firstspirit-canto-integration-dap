package com.canto.firstspirit.util;

import com.canto.firstspirit.service.CantoSaasServiceProjectBoundClient;
import de.espirit.firstspirit.client.plugin.report.Parameter;
import de.espirit.firstspirit.client.plugin.report.ParameterSelect.SelectItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * This class represents a folder structure obtained from a CantoSaasServiceProjectBoundClient. It generates a list of SelectItem objects representing the folder structure.
 */
public class FolderStructure {

  // List to store the folder structure as SelectItem objects
  private List<SelectItem> folderStructure = new ArrayList<>();

  /**
   * Constructs a FolderStructure object using a CantoSaasServiceProjectBoundClient. Retrieves the folder structure from the client and generates the list.
   *
   * @param cantoSaasServiceClient The CantoSaasServiceProjectBoundClient to fetch folder structure from.
   */
  public FolderStructure(CantoSaasServiceProjectBoundClient cantoSaasServiceClient) {

    // Fetch folder structure as string from the client
    String folderStructureString = cantoSaasServiceClient.fetchFolderStructure();

    // Add root item to the folder structure list
    addSelectItem("--Folder--", "--Folder--");

    if (folderStructureString != null) {
      // Convert folder structure string to JSONObject
      JSONObject jsonObject = new JSONObject(folderStructureString);

      // Generate folder structure list recursively
      generateList(jsonObject);

    }

  }

  /**
   * Recursively generates the list of folder structure from a JSONObject.
   *
   * @param jsonObject The JSONObject representing the folder structure.
   */
  private void generateList(JSONObject jsonObject) {
    // Extract results array from the JSONObject
    JSONArray results = jsonObject.getJSONArray("results");

    // Iterate through results array
    for (Object result : results) {
      JSONObject resultJSONObject = (JSONObject) result;
      String name = resultJSONObject.getString("name");
      // If result represents an album, add it to the folder structure list
      if (resultJSONObject.get("scheme")
          .equals("album")) {
        addSelectItem(name, resultJSONObject.getString("id"));
      }
      // Recursively get children of the current result
      getChildren(resultJSONObject, 0, name);
    }
  }

  /**
   * Recursively gets children of a JSONObject representing a folder or album.
   *
   * @param jsonObject The JSONObject representing the folder or album.
   * @param level      The depth level of the current folder or album.
   * @param parentName The name of the parent folder or album.
   */
  private void getChildren(JSONObject jsonObject, int level, String parentName) {
    if (jsonObject.has("children")) {
      level++;
      JSONArray children = jsonObject.getJSONArray("children");
      // Iterate through children array
      for (Object child : children) {
        JSONObject childJSONObject = (JSONObject) child;
        String name = parentName + "." + childJSONObject.getString("name");
        // If child represents an album, add it to the folder structure list
        if (childJSONObject.get("scheme")
            .equals("album")) {
          addSelectItem(name, childJSONObject.getString("id"));
        }
        // Recursively get children of the current child
        getChildren(childJSONObject, level, name);
      }
    }
  }

  /**
   * Adds a SelectItem to the folder structure list.
   *
   * @param name The name of the folder or album.
   * @param id   The ID of the folder or album.
   */
  private void addSelectItem(String name, String id) {
    folderStructure.add(Parameter.Factory.createSelectItem(name, id));
  }

  /**
   * Retrieves the folder structure list.
   *
   * @return The list of SelectItem objects representing the folder structure.
   */
  public List<SelectItem> getFolderStructure() {
    folderStructure.sort(Comparator.comparing(SelectItem::getLabel));
    return folderStructure;
  }

}