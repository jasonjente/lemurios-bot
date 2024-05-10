package bot.application.utils;

import bot.application.exceptions.ApplicationInitializationException;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility bean for reading values from a property file
 */

@Service
public class PropertiesUtil {
    private static final Map<String, String> lemurProps = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);
    private static final String LEMUR_PROPERTY_FILENAME = "lemurs.properties";

    private PropertiesUtil() {
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
            LOGGER.error(errorMessage, LEMUR_PROPERTY_FILENAME);
            // loading of the configuration file failed
            throw new ApplicationInitializationException(cex);
        }
    }

    public String getValue(String key) {
        return lemurProps.getOrDefault(key, null);
    }


}
