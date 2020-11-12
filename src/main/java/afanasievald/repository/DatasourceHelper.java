package afanasievald.repository;

import afanasievald.databaseEntity.Folder;
import afanasievald.databaseEntity.Photo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.JDBCException;
import org.jetbrains.annotations.NotNull;
import java.util.*;

public class DatasourceHelper {
    @NotNull
    private static final Logger LOGGER = LogManager.getLogger(DatasourceHelper.class.getName());

    private DatasourceHelper() {
    }

    /**
     * If there isn't any folders, returns empty map.
     * If there are folders, returns map of folders. Key in this map is folder's name.
     * If there some photos in this folder, than value in this map is identifier of photo
     * with minimum CreatedDate, otherwise null.
     *
     * @param folderRepository
     * @param photoRepository
     * @return an {@code Map<String, Long>}, if photos are present.
     * Key in this map is folder's name. If there some photos in this folder,
     * than value in this map is identifier of photo with minimum CreatedDate, otherwise null.
     * Otherwise empty {@code Map<String, Long>}, if there isn't any folders.
     */

    public static Map<String, Long> getFoldersWithPhoto(@NotNull FolderRepository folderRepository,
                                                        @NotNull PhotoRepository photoRepository) {
        Iterable<Folder> folders = folderRepository.findByOrderByCreatedDateAsc();
        Map<String, Long> foldersWithOnePhoto = new LinkedHashMap<>();
        for (Folder folder : folders) {
            List<Photo> photos = photoRepository.findByFolder(folder);
            if (!photos.isEmpty()) {
                photos.sort(Comparator.comparing(Photo::getCreatedDate));
                foldersWithOnePhoto.put(folder.getName(), photos.get(0).getIdentifier());
            } else {
                foldersWithOnePhoto.put(folder.getName(), null);
            }
        }
        return foldersWithOnePhoto;
    }

    /**
     * If a folder with this folderName is not present, returns null.
     * If a folder with this folderName is present, the folder doesn't contain photos,
     * returns empty list.
     * If a folder whit this folderName is present, the folder contains photos,
     * returns list of Photos sorted by CreatedDate.
     *
     * @param folderRepository
     * @param photoRepository
     * @param folderName
     * @return an {@code List<Photo>}, if a folder is present and contains photos,
     * otherwise empty {@code List<Photo>}, if folder is present and doesn't contain photo,
     * otherwise null {@code List<Photo>}, if folder isn't present.
     */
    public static List<Photo> getPhotosFromFolder(@NotNull FolderRepository folderRepository,
                                                  @NotNull PhotoRepository photoRepository,
                                                  @NotNull String folderName) {
        Optional<Folder> folder = folderRepository.findByName(folderName);
        if (!folder.isPresent()) {
            LOGGER.info(String.format("Folder %s doesn't exist in DB", folderName));
            return null;
        }

        List<Photo> sortedPhotos = photoRepository.findByFolder(folder.get());
        if (sortedPhotos.isEmpty()) {
            return sortedPhotos;
        }

        sortedPhotos.sort(Comparator.comparing(Photo::getCreatedDate));

        return sortedPhotos;
    }


    public static boolean savePhotoToFolder(@NotNull FolderRepository folderRepository,
                                            @NotNull PhotoRepository photoRepository,
                                            @NotNull String folderName,
                                            @NotNull Photo photo) {
        Optional<Folder> folder = folderRepository.findByName(folderName);
        if (!folder.isPresent()) {
            LOGGER.info(String.format("Folder %s doesn't exist", folderName));
            return false;
        }

        Optional<Photo> optPhoto = photoRepository.findByIdentifier(photo.getIdentifier());
        if (optPhoto.isPresent()) {
            LOGGER.info(String.format("Photo with identifier %d exists", photo.getIdentifier()));
            return false;
        }

        photo.setFolder(folder.get());
        try {
            photoRepository.save(photo);
            return true;
        } catch(JDBCException e){
            LOGGER.error(e);
            return false;
        }
    }

    public static boolean changeDescription(@NotNull PhotoRepository photoRepository,
                                            @NotNull Photo photo) {
        Optional<Photo> photoOptional = photoRepository.findByIdentifier(photo.getIdentifier());
        if (!photoOptional.isPresent()) {
            LOGGER.info(String.format("Photo with identifier %d doesn't exist", photo.getIdentifier()));
            return false;
        }

        Photo realPhoto = photoOptional.get();
        realPhoto.setDescription(photo.getDescription());
        try {
            photoRepository.save(realPhoto);
            return true;
        } catch(JDBCException e){
            LOGGER.error(e);
            return false;
        }
    }
}
