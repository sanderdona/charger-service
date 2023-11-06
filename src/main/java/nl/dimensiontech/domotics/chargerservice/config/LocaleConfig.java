package nl.dimensiontech.domotics.chargerservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
public class LocaleConfig {

    @Bean
    public void setLocale(){
        Locale.setDefault(new Locale("nl", "NL"));
    }
}
