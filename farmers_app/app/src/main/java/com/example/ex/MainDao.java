package com.example.ex1_205790488_315680397;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.google.firebase.firestore.auth.User;

import static androidx.room.OnConflictStrategy.REPLACE;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Dao
public interface MainDao {
    //inserttype
    @Insert(onConflict = REPLACE)
    void insert(UserTypeTable userTypeTable);

    //Update type
    @Query("Update type SET  type = :sType")
    void updateUserType (String sType);

    @Query("SELECT * FROM type")
    List<UserTypeTable> getAlltypes();

    @Query("SELECT type FROM type WHERE ID = :sId")
    String getUserType(int sId);

//delete all table
    @Query ("DELETE FROM crops")
    void nukeTableCrops();

    //insert query
    @Insert(onConflict = REPLACE)
    void insert(CropsTable cropsTable);

    //delete query
    @Delete
    void delete(CropsTable cropsTable);

    //Delete all query
    @Delete
    void reset(ArrayList<CropsTable> cropsTable);

    //Update crop name querry
    @Query("Update crops SET  cropName = :sCropName WHERE ID = :sID")
    void update (int sID, String sCropName);

    //Update crop name querry
    @Query("Update crops SET  season = :sCropSeason WHERE ID = :sID")
    void updateCropSeason(int sID, String sCropSeason);

    @Query("SELECT * FROM crops")
    List<CropsTable> getAllCrops();

    @Query("SELECT monthsPlantedTime FROM crops WHERE cropName = :sNameOfCrop")
    int getTimeToHarvest(String sNameOfCrop);

    @Query ("DELETE FROM seeds")
    void nukeTableSeeds();

    @Insert(onConflict = REPLACE)
    void insert(SeedsTable seedsTable);

    //delete query
    @Delete
    void delete(SeedsTable seedsTable);

    //Delete all query
    @Delete
    void resetSeeds(ArrayList<SeedsTable> seedsTable);

    //Update query
    @Query("Update seeds SET  seedName = :sSeedName WHERE ID = :sID")
    void updateSeedsName(int sID, String sSeedName);

    //Update query
    @Query("Update seeds SET  amount = :sSeedAmount WHERE ID = :sID")
    void updateSeedsAmount(int sID, String sSeedAmount);

    //Update query
    @Query("Update seeds SET  amount = :sSeedAmount WHERE seedName = :sNewName")
    void updateSeedsAmountByName(String sNewName, String sSeedAmount);

    @Query("SELECT amount FROM seeds WHERE seedName = :sNewName")
   int getSeedAmount(String sNewName);

    @Query("SELECT * FROM seeds")
    List<SeedsTable> getAllSeeds();

    @Query ("DELETE FROM messages")
    void nukeTableMessages();

    @Insert(onConflict = REPLACE)
    void insert(MessagesTable messagesTable);

    //delete query
    @Delete
    void delete(MessagesTable messagesTable);

    //Delete all query
    @Delete
    void resetMessagess(ArrayList<MessagesTable> messagesTable);

    @Query("SELECT * FROM  messages")
    List<MessagesTable> getAllMessages();

    @Query("SELECT * FROM  messages WHERE farmerBool = 1 ")
    List<MessagesTable> getAllFarmerUndeletedMessages();

    @Query("SELECT * FROM  messages WHERE agronomistBool = 1 ")
    List<MessagesTable> getAllAgronomistUndeletedMessages();

    //Update query
    @Query("Update messages SET  agronomistBool = :sAgronomistBool WHERE ID = :sID")
    void updateAgronomistBool(int sID, boolean sAgronomistBool);

    //Update query
    @Query("Update messages SET  farmerBool = :sFarmerBool WHERE ID = :sID")
    void updateFarmerBool(int sID, boolean sFarmerBool);

    @Query ("DELETE FROM ware")
    void nukeTableWare();

    @Insert(onConflict = REPLACE)
    void insert(WareTable wareTable);

    //delete query
    @Delete
    void delete(WareTable wareTable);

    //Delete all query
    @Delete
    void resetWareCrops(ArrayList<WareTable> wareTable);

    //Update query
    @Query("Update ware SET  WareCropName = :sCropName WHERE ID = :sID")
    void updateWareCropName(int sID, String sCropName);

    //Update query
    @Query("Update ware SET  amount = :sCropAmount WHERE ID = :sID")
    void updateWareCropAmount(int sID, String sCropAmount);

    @Query("Update ware SET  amount = :sNewWareAmount WHERE wareCropName = :sNewCropName")
    void updateWareCropAmountByName(String sNewWareAmount, String sNewCropName);

    @Query("SELECT amount FROM ware WHERE wareCropName = :sCropName")
    int getWareCropAmount(String sCropName);

    @Query("SELECT * FROM ware")
    List<WareTable> getAllWareCrops();

    @Query ("DELETE FROM plots")
    void nukeTablePlots();

    @Insert(onConflict = REPLACE)
    void insert(PlotTable plotTable);

    //delete query
    @Delete
    void delete(PlotTable plotTable);

    //Delete all query
    @Delete
    void resetPlots(ArrayList<PlotTable> plotTable);

    //Update query
    @Query("Update plots SET  plantedCropName = :sPlantedCropName WHERE ID = :sID")
    void updatePlantedCropName(int sID, String sPlantedCropName);

    //Update query
    @Query("Update plots SET  amount = :sCropAmount WHERE ID = :sID")
    void updatePlantedCropAmount(int sID, String sCropAmount);

    //Update query
    @Query("Update plots SET  dateOfHarvest = :sDateOfHarvest WHERE ID = :sID")
    void updatePlantedCropDateOfHarvest(int sID, long sDateOfHarvest);

    //Update query
    @Query("Update plots SET `row` = :sPlotRow WHERE ID = :sID")
    void updatePlotRow(int sID, int sPlotRow);

    //Update query
    @Query("Update plots SET  `column` = :sPlotColumn WHERE ID = :sID")
    void updatePlotColumn(int sID, int sPlotColumn);


    @Query("SELECT * FROM plots")
    List<PlotTable> getAllPlots();


    @Insert(onConflict = REPLACE)
    void insert(SupplierTable supplierTable);

    //delete query
    @Delete
    void delete(SupplierTable supplierTable);

    //Delete all query
    @Delete
    void resetSupplierTable(ArrayList<SupplierTable> supplierTable);

    //Update query
    @Query("Update supplier SET cropName = :sCropName WHERE ID = :sID")
    void updateSupplierCropName(int sID, String sCropName);

    //Update query
    @Query("Update supplier SET  price = :sCropPrice WHERE ID = :sID")
    void updateSupplierCropPrice(int sID, String sCropPrice);

    //Update query
    @Query("Update allsupplier SET  price = :sCropPrice WHERE cropName = :cCropName AND mail =:cMail")
    void updateAllSupplierCropPrice(String cMail,String cCropName, String sCropPrice);

    //delete query
//    @Delete
//    void deleteFromAllsuplliersItems(AllSuppliersItemsTable allSuppliersItemsTable);

    @Query("Delete FROM allsupplier WHERE cropName = :cCropName AND mail = :cMail")
    void deleteAllSupplierItemsTable(String cMail, String cCropName);

    @Insert(onConflict = REPLACE)
    void insert(AllSuppliersItemsTable allSuppliersItemsTable);

    @Query("SELECT * FROM allsupplier")
    List<AllSuppliersItemsTable> getAllsupplierItemsForFarmer();

    @Query ("DELETE FROM allsupplier")
    void nukeTableAllSupplier();

    @Query("SELECT * FROM supplier")
    List<SupplierTable> getAllsupplierItems();

    @Query ("DELETE FROM supplier")
    void nukeTableSupplier();

}
