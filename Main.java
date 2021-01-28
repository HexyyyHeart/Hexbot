import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.Timer;
import java.util.TimerTask;

//GUILD ID 677299287813783594
public class Main extends ListenerAdapter
{
    long Guild = 677299287813783594L;
    static JDA jda;
    static long botRequestDelay = 9000;
    int i = 0;
    TimerTask botRequestClear = new TimerTask(){
        public void run()
        {
            System.out.println("Clearing bot request thread");
            jda.getGuildById(Guild).getTextChannelsByName("bot-testing", true).get(0).deleteMessages(
                jda.getGuildById(Guild).getTextChannelsByName("bot-testing", true).get(0).getHistoryAfter(804181244984164453L,1000).complete().getRetrievedHistory());
        }
    };
    Timer botRequestClearTimer = new Timer();

    public static void main(String[] args) throws LoginException
    {
        //Hexbot: ODAzOTkwNzg0MzY1Mjk3Njg0.YBF0xA.mgZWlUg-OY6SCAkau2fL-vkdqSM
        //HexbotDev: ODA0NDIyMTgzMDY0Njk4OTEw.YBMGiQ.q2Cv45H6uCpJvDEPqUg4glPShaI
        JDABuilder builder = JDABuilder.create("ODA0NDIyMTgzMDY0Njk4OTEw.YBMGiQ.q2Cv45H6uCpJvDEPqUg4glPShaI", GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_TYPING);
        builder.addEventListeners(new Main());
        jda = builder.build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        System.out.println(event.getChannel().getName() +"| "+  event.getAuthor().getName() + ": "+ event.getMessage().getContentDisplay());

        if(event.getChannel().equals(event.getGuild().getTextChannelsByName("bot-testing", true)))
        {
            botRequestClearTimer.schedule(botRequestClear, botRequestDelay);
        }

        if(event.getMessage().getContentRaw().equals("!wakeup"))
        {
            event.getChannel().sendMessage("I have awoken from my dark slumber, prepare to be dominated.").queue();
        }
        else if(event.getMessage().getContentRaw().equals("!resetspamcounter"))
        {
            i = 0;
            event.getChannel().sendMessage("Done.").queue();
        }
        else if(event.getMessage().getContentRaw().contains("!spam"))
        {
            i++;
            if(i<=20)
            event.getChannel().sendMessage("Like this? !spam").queue();
        }
        else if(event.getMessage().getContentRaw().contains("!scream"))
        {
            if(event.getAuthor().getName().equalsIgnoreCase("Hexyyy<3"))
                event.getGuild().getTextChannelsByName("scream", true).get(0)
                    .sendMessage("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").queue();
            else
                event.getChannel().sendMessage("You do not have access to that command.").queue();
        }

    }
    @Override
    public void onUserTyping(UserTypingEvent event)
    {
        System.out.println( event.getUser().getName() + "Started Typing in  "+ event.getChannel().getName());
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event)
    {
        /*
        He/Him:   ROLE: 804128546120794112 EMOJI: :blue_heart:
        They/Them:ROLE: 804128631542513664 EMOJI: :purple_heart:
        She/Her:  ROLE: 804128667219132416 EMOJI: :heart:           \u2764\ufe0f
         */

        if(event.getMessageId().equals("804403902921179136"))
        {
            String emoji = event.getReactionEmote().getName();

            System.out.println(event.getChannel().getName() +"| "+ event.getUser().getName()+": Responded with: "+ (emoji));

            if(emoji.equalsIgnoreCase("\u2764\ufe0f"))
                event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("804128667219132416")).queue(); //add She/Her role to user
            else if(emoji.equalsIgnoreCase("\uD83D\uDC99"))
                event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("804128546120794112")).queue(); //add He/Him role to user
            else if(emoji.equalsIgnoreCase("\uD83D\uDC9C"))
                event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("804128631542513664")).queue(); //add They/Them role to user
        }
    }

    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event)    {
        if(event.getMessageId().equals("804403902921179136"))
        {
            String emoji = event.getReactionEmote().getName();

            System.out.println(event.getChannel().getName() +"| "+ event.getUser().getName()+": Removed: "+ (emoji));

            if(emoji.equalsIgnoreCase("\u2764\ufe0f"))
                event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById("804128667219132416")).queue(); //remove She/Her role to user
            else if(emoji.equalsIgnoreCase("\uD83D\uDC99"))
                event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById("804128546120794112")).queue(); //remove He/Him role to user
            else if(emoji.equalsIgnoreCase("\uD83D\uDC9C"))
                event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById("804128631542513664")).queue(); //remove They/Them role to user
        }
    }
}
