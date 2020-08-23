package pass.keep.utils;

import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import pass.keep.dto.CredentialsDto;
import pass.keep.entities.CredentialsEntity;
import pass.keep.exceptions.DbUnavailableException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class DbUtil {

    private static final String DB_FILE_UNAVAILABLE = "Database file is unavailable";
    private static final String DB_MAP_NAME = "default";

    private static final File DB_FILE = FileUtil.getDbFile();

    public static List<CredentialsDto> loadCredentials() throws DbUnavailableException {
        if (DB_FILE == null) {
            throw new DbUnavailableException(DB_FILE_UNAVAILABLE);
        }

        try (DB db = DBMaker.fileDB(DB_FILE).make()) {
            ConcurrentMap<String, CredentialsEntity> map = db.hashMap(DB_MAP_NAME, Serializer.STRING, Serializer.JAVA).createOrOpen();

            List<CredentialsDto> credentials = new ArrayList<>(map.size());
            map.values().forEach(entity -> credentials.add(CredentialsDto.of(entity)));
            log.info("Successfully loaded {} objects from database", credentials.size());
            return credentials;
        } catch (Exception e) {
            log.error("Cannot load objects from database", e);
            throw new DbUnavailableException(e);
        }
    }

    public static void saveCredentials(CredentialsDto dto) throws DbUnavailableException {
        if (DB_FILE == null) {
            throw new DbUnavailableException(DB_FILE_UNAVAILABLE);
        }

        try (DB db = DBMaker.fileDB(DB_FILE).make()) {
            CredentialsEntity entity = CredentialsEntity.of(dto);

            ConcurrentMap<String, CredentialsEntity> map = db.hashMap(DB_MAP_NAME, Serializer.STRING, Serializer.JAVA).createOrOpen();
            map.put(entity.getSecretKey(), entity);
            log.info("Successfully saved {} in database", entity);
        } catch (Exception e) {
            log.error("Cannot save object in database", e);
            throw new DbUnavailableException(e);
        }
    }
}
