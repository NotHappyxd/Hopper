package me.happy.license.server.discord;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import me.happy.license.server.License;
import me.happy.license.server.Server;
import me.happy.license.server.util.LicenseUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.isWebhookMessage() || event.getAuthor().isFake() || event.getAuthor().isBot()) return;

        String message = event.getMessage().getContentRaw();
        if (message.startsWith("!")) {
            if (event.getMember() == null || !event.getMember().hasPermission(Permission.ADMINISTRATOR)) return;
            String[] args = message.split("\\s");

            switch (args[0].toLowerCase()) {
                case "createlicense": {
                    // !createlicense <clientname> <ipLimit>
                    if (args.length != 4) {
                        event.getTextChannel().sendMessage("Invalid usage. !createlicense <clientname> <iplimit> <expiresIn>").queue();
                        event.getMessage().delete().queue();
                        return;
                    }

                    long parse = LicenseUtil.parse(args[3]);

                    int ipLimit = LicenseUtil.parseInt(args[2]);

                    if (parse == 0) {
                        event.getTextChannel().sendMessage("The time that the license expires in is invalid.").queue();
                        event.getMessage().delete().queue();
                        return;
                    }

                    if (ipLimit == 0) {
                        event.getTextChannel().sendMessage("The ip limit is invalid.").queue();
                        event.getMessage().delete().queue();
                        return;
                    }

                    License license = new License(LicenseUtil.getRandomLicense(), args[1], System.currentTimeMillis(),
                            parse, ipLimit);

                    MongoCollection<Document> collection = Server.getClient().getDatabase("license").getCollection("licenses");

                    Document document = new Document();
                    document.put("license", license.getLicense());
                    document.put("clientName", license.getClientName());
                    document.put("addedAt", license.getAddedAt());
                    document.put("expiresIn", license.getExpiresIn());
                    document.put("ipLimit", license.getIpLimit());

                    collection.replaceOne(Filters.eq("license", license.getLicense()), document, new UpdateOptions().upsert(true));
                    Server.getLicenses().add(license);

                    break;
                }

                case "deletelicense": {
                    if (args.length != 2) {
                        event.getTextChannel().sendMessage("Invalid usage. !deletelicense <license>").queue();
                        event.getMessage().delete().queue();
                        return;
                    }

                    String key = args[1];

                    License license = Server.getLicenses().stream().filter(license1 -> license1.getLicense().equalsIgnoreCase(key)).findFirst().orElse(null);
                    if (license == null) {
                        event.getTextChannel().sendMessage("That license does not match anyone in our database!").queue();
                        event.getMessage().delete().queue();
                        return;
                    }

                    Server.getClient().getDatabase("license").getCollection("licenses").deleteOne(Filters.eq("license", license.getLicense()));

                    Server.getLicenses().remove(license);

                    event.getTextChannel().sendMessage(license.getLicense() + " was successfully deleted!").queue();

                    break;
                }

                case "licenseinfo": {
                    if (args.length != 2) {
                        event.getTextChannel().sendMessage("Invalid usage. !licenseinfo <license>").queue();
                        event.getMessage().delete().queue();
                        return;
                    }

                    String key = args[1];

                    License license = Server.getLicenses().stream().filter(license1 -> license1.getLicense().equalsIgnoreCase(key)).findFirst().orElse(null);

                    if (license == null) {
                        event.getTextChannel().sendMessage("That license does not match any licenses in our database!").queue();
                        event.getMessage().delete().queue();
                        return;
                    }

                    EmbedBuilder builder = new EmbedBuilder().setTitle("Info for " + license.getLicense())
                            .addField("Client Name", license.getClientName(), true)
                            .addField("IP Limit", String.valueOf(license.getIpLimit()), true)
                            .addField("Expires In", (license.getExpiresIn() == Long.MAX_VALUE ? "Never"
                                    : LicenseUtil.formatTimeMillis(license.getAddedAt() - license.getExpiresIn())), false)
                            .addField("IPS", String.join(", ", license.getIps()), false);

                    event.getTextChannel().sendMessage(builder.toString()).queue();
                    break;
                }
            }
        }
    }
}
