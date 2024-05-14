import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import lombok.extern.slf4j.Slf4j;
import misc.CustomTab;
import store.CacheLibrary;
import store.cache.index.OSRSIndices;
import store.cache.index.archive.Archive;
import store.plugin.extension.FXController;
import store.utilities.GZIPCompressor;
import suite.Constants;
import suite.Main;
import suite.controller.Selection;
import suite.dialogue.Dialogues;
import utility.*;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author ReverendDread & RSPSi on 11/27/2019
 * @project ValkyrCacheSuite
 */
@Slf4j
public class MapPacker extends FXController {

	private CustomTab tab;
	@FXML
	private RadioButton folderRadio;

	@FXML
	private ToggleGroup loadType;

	@FXML
	private VBox folderFileBox;

	@FXML
	private TextField mapFolderText;

	@FXML
	private Button mapFolderBrowse;

	@FXML
	private RadioButton singleFileRadio;

	@FXML
	private VBox singleFileBox;

	@FXML
	private TextField lsText;

	@FXML
	private Button lsBrowse;

	@FXML
	private TextField objText;

	@FXML
	private Button objBrowse;

	@FXML
	private RadioButton regionIdRadio;

	@FXML
	private ToggleGroup regionType;

	@FXML
	private HBox regionIdBox;

	@FXML
	private TextField regionID;

	@FXML
	private RadioButton regionXYRadio;

	@FXML
	private HBox regionXYBox;

	@FXML
	private TextField regionXText;

	@FXML
	private TextField regionYText;

	@FXML
	private RadioButton worldRadio;

	@FXML
	private HBox worldXYBox;

	@FXML
	private TextField worldXText;

	@FXML
	private TextField worldYText;

	@FXML
	private TextField xteaText;

	@FXML
	private Button genXteas;

	@FXML
	private Button saveBtn;

	@FXML
	private Canvas canvas;

	@FXML
	private AnchorPane worldAnchor;

	@FXML
	private TextField regionIdField;

	@FXML
	private Button regionIdSubmit;

//	private WorldMapRenderer renderer;

    private List<XTEA> xteaStore = Lists.newArrayList();

//    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

//    public void saveXTeas(){
//        if(Constants.settings.xteaFile == null || Constants.settings.xteaFile.isEmpty())
//            return;
//
//        try(FileWriter fw = new FileWriter(new File(Constants.settings.xteaFile))){
//            GSON.toJson(xteaStore, fw);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

	@Override
	public void initialize(CustomTab tab, boolean refresh, int lastId) {

        if (Constants.settings.xteaFile == null || Constants.settings.xteaFile.isEmpty()) {
            File file = RetentionFileChooser.showOpenDialog("Please select xteas.json", Main.getSelection().stage.getScene().getWindow(), FilterMode.JSON);
            if (file != null) {
                Constants.settings.xteaFile = file.getAbsolutePath();
                Constants.settings.save();
            }
        }

//        if(Constants.settings.xteaFile != null && !Constants.settings.xteaFile.isEmpty()){
//            try(FileReader fr = new FileReader(new File(Constants.settings.xteaFile))) {
//                xteaStore = GSON.fromJson(fr, new TypeToken<List<XTEA>>() { }.getType());
//            } catch (Exception ex){
//                ex.printStackTrace();
//            }
//        }

//		renderer = new WorldMapRenderer(canvas);
//		new Thread(renderer).start();

		XTEASManager.get().setParserType(XTEASManager.XTEAParserType.POLAR);

	    regionType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            regionIdBox.setDisable(newValue != regionIdRadio);
            regionXYBox.setDisable(newValue != regionXYRadio);
            worldXYBox.setDisable(newValue != worldRadio);
        });

	    loadType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            folderFileBox.setDisable(newValue != folderRadio);
            singleFileBox.setDisable(newValue != singleFileRadio);
        });
		this.tab = tab;
		mapFolderBrowse.setOnAction(evt -> {
            File file = RetentionFileChooser.showOpenDialog(Main.getSelection().stage.getScene().getWindow(), FilterMode.PACK);
            if (file != null) {
                mapFolderText.setText(file.getAbsoluteFile().toString());
            }
        });
		lsBrowse.setOnAction(evt -> {
			File file = RetentionFileChooser.showOpenDialog(Main.getSelection().stage.getScene().getWindow(), FilterMode.DAT, FilterMode.GZIP);
			if (file != null) {
				lsText.setText(file.getAbsoluteFile().toString());
			}
		});
		objBrowse.setOnAction(evt -> {
			File file = RetentionFileChooser.showOpenDialog(Main.getSelection().stage.getScene().getWindow(), FilterMode.DAT, FilterMode.GZIP);
			if (file != null) {
				objText.setText(file.getAbsoluteFile().toString());
			}
		});
		saveBtn.setOnAction(evt -> {

			int regionX, regionY;
			int regionId = Integer.parseInt(regionID.getText());
			if (regionType.getSelectedToggle() == regionIdRadio) {
				regionX = regionId >> 8;
				regionY = regionId & 0xFF;
			} else if (regionType.getSelectedToggle() == regionXYRadio) {
				regionX = Integer.parseInt(regionXText.getText());
				regionY = Integer.parseInt(regionYText.getText());
			} else {
				regionX = (int) Math.floor(Integer.parseInt(worldXText.getText()) / 64.0);
				regionY = (int) Math.floor(Integer.parseInt(worldYText.getText()) / 64.0);
			}

			int[] xteas = xteaText.getText().isEmpty() ? XTEASManager.lookup(regionId) :
					Arrays.stream(xteaText.getText().replace(" ", "").split(",")).mapToInt(Integer::parseInt).toArray();

			if (Objects.isNull(xteas)) {
				xteas = generateXTEAS();
				log.info("Generating xteas for region {}, {}", regionId, xteas);
			}

			if (xteas.length < 4) {
				Dialogues.alert(Alert.AlertType.ERROR, "XTEA Format Incorrect", "Please check your xteas format", "needs to be - 0, 0, 0, 0", false);
				return;
			}

			Map<Integer, int[]> xteaKeys = Maps.newConcurrentMap();

			if (loadType.getSelectedToggle() == folderRadio) {
				if (mapFolderText.getText().isEmpty()) {
					Alert alert = new Alert(Alert.AlertType.ERROR, "Please select map files!", ButtonType.OK);
					alert.show();
					return;
				}
				if (!(mapFolderText.getText().endsWith(".pack"))) {
					Alert alert = new Alert(Alert.AlertType.ERROR, "File needs to be .pack format!", ButtonType.OK);
					alert.show();
					return;
				}
				try {
					List<MultiMapEncoder.Chunk> chunks = MultiMapEncoder.decode(Files.readAllBytes(new File(mapFolderText.getText()).toPath()));
					int[] finalXteas = xteas;
					chunks.stream()
					.map(chunk -> {
						int localRegionId = (regionX + chunk.offsetX) << 8 | (regionY + chunk.offsetY);
						xteaStore.removeIf(xtea -> xtea.getRegion() == localRegionId);
						xteaStore.add(new XTEA(localRegionId, finalXteas));
						return packMaps(regionX + (chunk.offsetX), regionY + (chunk.offsetY), chunk.tileMapData, chunk.objectMapData, finalXteas);
					})
					.forEach(map -> map.entrySet().forEach(entry -> xteaKeys.put(entry.getKey(), entry.getValue())));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				if (lsText.getText().isEmpty() || objText.getText().isEmpty()) {
					Alert alert = new Alert(Alert.AlertType.ERROR, "Please select map files!", ButtonType.OK);
					alert.show();
					return;
				}
				if (!(lsText.getText().endsWith(".gz") || lsText.getText().endsWith(".dat")) || !(objText.getText().endsWith(".gz") || objText.getText().endsWith(".dat"))) {
					Alert alert = new Alert(Alert.AlertType.ERROR, "Map files need to be .gz or .dat format!", ButtonType.OK);
					alert.show();
					return;
				}
				try {
					byte[] tileData = Files.readAllBytes(new File(lsText.getText()).toPath());
					byte[] objData = Files.readAllBytes(new File(objText.getText()).toPath());

					if (objText.getText().endsWith(".gz")) {
						objData = GZIPCompressor.inflate317(objData);
					}
					if (lsText.getText().endsWith(".gz")) {
						tileData = GZIPCompressor.inflate317(tileData);
					}

					xteaKeys.putAll(packMaps(regionX, regionY, tileData, objData, xteas));

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			if(CacheLibrary.get().getIndex(OSRSIndices.MAPS).update(Selection.progressListener, xteaKeys)){
				Alert alert = new Alert(Alert.AlertType.INFORMATION, "Maps packed successfully!", ButtonType.OK);
				alert.show();
            }

		});
		genXteas.setOnAction(event -> {
			generateXTEAS();
		});
		canvas.widthProperty().bind(worldAnchor.widthProperty());
		canvas.heightProperty().bind(worldAnchor.heightProperty());
		regionIdSubmit.setOnAction((e) -> {
			try {
				int id = Integer.parseInt(regionIdField.getText());
//				renderer.setRegionId(id);
			} catch (Exception ex) {
//				renderer.setRegionId(-1);
			}
		});
	}

	public int[] generateXTEAS() {
		List<Integer> keys = Lists.newArrayList();
		Random random = new Random();
		keys.add(random.nextInt());
		keys.add(random.nextInt());
		keys.add(random.nextInt());
		keys.add(random.nextInt());
		xteaText.setText(keys.stream().map(String::valueOf).collect(Collectors.joining(",")));
		return keys.stream().mapToInt(Integer::intValue).toArray();
	}

	public Map<Integer, int[]> packMaps(int regionX, int regionY, byte[] tileData, byte[] objData, int[] xteas) {
		try {

			String mapArchiveName = "m" + regionX + "_" + regionY;
			String landArchiveName = "l" + regionX + "_" + regionY;

			Archive mapArchive = CacheLibrary.get().getIndex(OSRSIndices.MAPS).getArchive(mapArchiveName);
			boolean exists = Objects.nonNull(mapArchive);

			if (exists) {
				mapArchive.reset();
			} else {
				mapArchive = CacheLibrary.get().getIndex(OSRSIndices.MAPS).addArchive(mapArchiveName);
			}
			mapArchive.addFile(0, tileData);
			mapArchive.flag();

			Archive landArchive = CacheLibrary.get().getIndex(OSRSIndices.MAPS).getArchive(landArchiveName);
			exists = Objects.nonNull(landArchive);

			if (exists) {
				landArchive.reset();
			} else {
				landArchive = CacheLibrary.get().getIndex(OSRSIndices.MAPS).addArchive(landArchiveName);
			}
			landArchive.addFile(0, objData);
			landArchive.flag();

			Map<Integer, int[]> xteaKeys = Maps.newHashMap();
			xteaKeys.put(landArchive.getId(), xteas);

			return xteaKeys;
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to pack maps!", ButtonType.OK);
			alert.show();
			return Maps.newHashMap();
		}
	}

	@Override
	public void save() {

	}

	@Override
	public ConfigEditorInfo getInfo() {
		return null;
	}
}
