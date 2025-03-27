package bot.application.utils;

import bot.application.exceptions.ApplicationInitializationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility bean for reading values from a property file
 */

@Service
@Slf4j
public class ConfigurationFileAccessor {
    private static final Map<String, String> lemurProps = new HashMap<>();
    
    private static final String LEMUR_PROPERTY_FILENAME = "lemurs.properties";

    private ConfigurationFileAccessor() {
    }

    @PostConstruct
    public void init() throws ApplicationInitializationException {
        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties()
                                .setFileName(LEMUR_PROPERTY_FILENAME));
        try {
            Configuration config = builder.getConfiguration();
            config.getKeys().forEachRemaining(key -> lemurProps.put(key, config.getString(key)));
        } catch (ConfigurationException cex) {
            String errorMessage = "Error importing properties file {}," +
                    " make sure that the file exists and that it has the proper access rights and encoding.";
            log.error(errorMessage, LEMUR_PROPERTY_FILENAME);
            // loading of the configuration file failed
            throw new ApplicationInitializationException(cex);
        }
    }

    public String getValue(String key) {
        return lemurProps.getOrDefault(key, null);
    }


}
