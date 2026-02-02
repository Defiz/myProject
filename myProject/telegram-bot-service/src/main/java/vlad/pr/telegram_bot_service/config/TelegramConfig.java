package vlad.pr.telegram_bot_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import vlad.pr.telegram_bot_service.bot.MyTelegramBot;

@Configuration
public class TelegramConfig {
    @Bean
    public TelegramClient telegramClient(MyTelegramBot myTelegramBot) {
        return new OkHttpTelegramClient(myTelegramBot.getBotToken());
    }
}
