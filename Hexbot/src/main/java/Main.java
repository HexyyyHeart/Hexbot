import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

//GUILD ID 677299287813783594
public class Main extends ListenerAdapter
{
   // long Guild = 677299287813783594L; //Guild 14
    long Guild = 634517741251985418L; //personal server
    static JDA jda;
    long botRequestDelay = 12000000;
    int i = 0;
    HashMap<String, String[]> triggerMap = new HashMap<String, String[]>();
    //HashMap<String, String> spoilerMap = new HashMap<String, String>();

    TimerTask botRequestClear = new TimerTask(){
        public void run()
        {
            try {
            System.out.println("Clearing bot request thread");
            jda.getGuildById(Guild).getTextChannelsByName("bot-requests-and-services", true).get(0).deleteMessages(
                jda.getGuildById(Guild).getTextChannelsByName("bot-requests-and-services", true).get(0).getHistoryAfter(804403902921179136L,100).complete().getRetrievedHistory()).queue();
                botRequestClearTimer.schedule(botRequestClear, botRequestDelay);
            } catch (Exception e) {
			// Here is the most abnormal hit in the log, easy to check the problem
                e.printStackTrace();
            }
        }
    };
    Timer botRequestClearTimer = new Timer();

    public static void main(String[] args) throws LoginException
    {
        //Hexbot: REMOVED FOR GITHUB
        //HexbotDev: REMOVED FOR GITHUB
        JDABuilder builder = JDABuilder.create("REMOVED FOR GITHUB", GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_TYPING);
        builder.addEventListeners(new Main());
        jda = builder.build();

    }
    @Override
    public void onReady(@Nonnull ReadyEvent event)
    {
        try {
            String[] triggerWordFilenames = new File("TriggerWords").list();

            for(String  fileName: triggerWordFilenames)
            {
                BufferedReader reader = new BufferedReader(new FileReader("TriggerWords/" + fileName));
                fileName = fileName.replaceAll(".txt", "");

                // Read lines from file.
                while (true) {
                    String line = reader.readLine();
                    if (line == null)
                        break;
                    // Split line on comma.
                    triggerMap.put(fileName, line.split(", "));     //arrayOfTriggerWords = line.split(", ");

                }

                reader.close();
            }


        } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }

    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        int j = 0;
        String message = event.getMessage().getContentRaw();
       // String arrayofTriggerWords[] = new System.in.read();


        System.out.println(event.getChannel().getName() +"| "+  event.getAuthor().getName() + ": "+ event.getMessage().getContentDisplay());

       /* if(event.getChannel().equals(event.getGuild().getTextChannelsByName("bot-testing", true).get(0)))
        {
            botRequestClearTimer.cancel();
            botRequestClearTimer.purge();
            botRequestClearTimer.schedule(botRequestClear, botRequestDelay);
           if(message.contains("@Hexyyy<#"))
            jda.getUserById(276531511417896971L).openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(event.getMessage().getContentDisplay()));
        }*/

        if(message.equals("!wakeup"))
        {
            event.getChannel().sendMessage("I have awoken from my dark slumber, prepare to be dominated.").queue();
        }
        else if(message.equals("!resetspamcounter"))
        {
            i = 0;
            event.getChannel().sendMessage("Done.").queue();
        }
        else if(message.contains("!spam"))
        {
            i++;
            if(i<=20)
            event.getChannel().sendMessage("Like this? !spam").queue();
        }
        else if(message.contains("!scream"))
        {
            if(event.getAuthor().getName().equalsIgnoreCase("Hexyyy<3"))
                event.getGuild().getTextChannelsByName("scream", true).get(0)
                    .sendMessage("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").queue();
            else
                event.getChannel().sendMessage("You do not have access to that command.").queue();
        }
        else if(message.contains("!report"))
        {
            report(event, message);
        }
        triggerSniffer(event, message);


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
        Xe/Xir:   ROLE:
         */

        if(event.getMessageId().equals("804403902921179136"))
        {
            addRole(event);
        }
       /* else if(event.retrieveMessage().complete().getContentRaw().contains("||React to this post to be DM'd the contents||"))
        {
            //DM Emoji user with saved content
            event.retrieveMember().complete().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(spoilerMap.get(event.getMessageId())).queue());

        }*/
    }

    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event)
    {

        if(event.getMessageId().equals("804403902921179136"))
        {
            removeRole(event);
        }
    }

    String trimWord(String s, int wordNum)
    {
        try {
            String[] sp = s.split(" ");
            return sp[wordNum];
        }catch(ArrayIndexOutOfBoundsException e){
            return "0";
        }
    }

    void addSpoiler(MessageReceivedEvent event, String tag, String post, String channel)
    {
        /*
        1. Copy Message contents to file
        2. Delete message
        3. Post Content warning with tag
        4. Await Emoji reply
        5. DM Emoji user with saved content
         */

        if(!event.getAuthor().isBot())
        {
            Message spoilerSource = event.getGuild().getTextChannelById(channel).retrieveMessageById(post).complete();
            //Post Content Warning
            // Copy Message
            event.getChannel().sendMessage(event.getAuthor().getName() +" CW: "+tag+ "\n ||"+spoilerSource.getContentRaw()+"||").queue();

            //Delete message
            spoilerSource.delete().queue();
        }


    }
    void addRole(GuildMessageReactionAddEvent event)
    {
        String emoji = event.getReactionEmote().getName();

        System.out.println(event.getChannel().getName() +"| "+ event.getUser().getName()+": Responded with: "+ (emoji));

        if(emoji.equalsIgnoreCase("\u2764\ufe0f"))
            event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("804128667219132416")).queue(); //add She/Her role to user
        else if(emoji.equalsIgnoreCase("\uD83D\uDC99"))
            event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("804128546120794112")).queue(); //add He/Him role to user
        else if(emoji.equalsIgnoreCase("\uD83D\uDC9C"))
            event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("804128631542513664")).queue(); //add They/Them role to user
        else if(emoji.equalsIgnoreCase("\uD83D\uDC9A"))
            event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("804437729651064902")).queue(); //add Xe/Xir role to user
    }

    void removeRole(GuildMessageReactionRemoveEvent event)
    {
        String emoji = event.getReactionEmote().getName();

        //System.out.println(event.getChannel().getName() +"| "+ event.getUser().getName()+": Removed: "+ (emoji));

        if(emoji.equalsIgnoreCase("\u2764\ufe0f"))
            event.getGuild().removeRoleFromMember(event.retrieveMember().complete(), event.getGuild().getRoleById("804128667219132416")).queue(); //remove She/Her role from user
        else if(emoji.equalsIgnoreCase("\uD83D\uDC99"))
            event.getGuild().removeRoleFromMember(event.retrieveMember().complete(), event.getGuild().getRoleById("804128546120794112")).queue(); //remove He/Him role from user
        else if(emoji.equalsIgnoreCase("\uD83D\uDC9C"))
            event.getGuild().removeRoleFromMember(event.retrieveMember().complete(), event.getGuild().getRoleById("804128631542513664")).queue(); //remove They/Them role from user
        else if(emoji.equalsIgnoreCase("\uD83D\uDC9A"))
            event.getGuild().removeRoleFromMember(event.retrieveMember().complete(), event.getGuild().getRoleById("804437729651064902")).queue(); //remove Xe/Xir role from user
    }

    void report(MessageReceivedEvent event, String message)
    {
         /*  --------Report Functionality-----------------
            commands:
                -cw: accept tag and post-id |output| spoiler warning tag.
                -addTrigger: accept contentWarning and triggerWord |Output| add triggerWord to proper category.


             */
        String command = trimWord(message,     1);

        if(command.equals("0"))
        {
            event.getChannel().sendMessage("Invalid Command").queue();
        }
        else if(command.equalsIgnoreCase("-cw"))
        {
            //cw ; content warning functionality
            //!report (!report -cw "contentWarning" MessageID ChannelID)
            String contentWarning = trimWord(message,     2);
            String messageID = trimWord(message,    3);
            String channelID = trimWord(message, 4);
            if((contentWarning != "0"&&messageID != "0"&&channelID != "0"))
                addSpoiler(event, contentWarning, messageID, channelID);
            else
                event.getChannel().sendMessage("Invalid Command").queue();
        }
        else if(command.equalsIgnoreCase("-addTrigger"))
            addTrigger(event, message);

        else if(command.equalsIgnoreCase("-viewTriggers"))
            viewTriggers(event, message);
        else if(command.equalsIgnoreCase("-viewCWCategories"))
        {
            event.getChannel().sendMessage(triggerMap.keySet().toString()).queue();
        }
    }

    void addTrigger(MessageReceivedEvent event, String message)
    {

        //!report -addTrigger "tag" "triggerWord"
        String contentWarning = trimWord(message, 2);
        String triggerWord = trimWord(message, 3);
        if(contentWarning  !=  "0" &&  triggerWord !=  "0")
        {

                   /*
                   	-command to add word to category
		                !report -addTrigger "contentWarning" "triggerWord"
		                    for the case of existing "contentWarning" category:
		                        for the case of word is not already in list:
			                        add "triggerWord" to array "contentWarning" and update "contentWarning".txt
			                    Otherwise:
			                        Say "word is already in list"
		                    for the case of nonexistent  "contentWarning" category:
			                    add "contentWarning" to the list of arrays,
			                    add "triggerWord" to new "contentWarning" array
			                    create a new "contentWarning".txt file containing "triggerWord"
                    */
            if(triggerMap.containsKey(contentWarning))                                              // for the case of existing "contentWarning" category:
            {

                for(String word: triggerMap.get(contentWarning))
                    if(word.equalsIgnoreCase(triggerWord))                                                          // for the case of word is not already in list:
                    {
                        event.getChannel().sendMessage("That word is already in a list").queue();  // Say "word is already in list"
                        return;
                    }
                // Otherwise:
                // add "triggerWord" to array "contentWarning"
                ArrayList<String> tempCategory = new ArrayList<String>();
                for (String word : triggerMap.get(contentWarning)) tempCategory.add(word);      // add all the words in the category to the temporary arrayList
                tempCategory.add(triggerWord);                                                  // add the trigger word to the list of words
                String tempCategory2[] = tempCategory.toArray(new String[tempCategory.size()]); // move all of the words in the array list back into an array

                triggerMap.replace(contentWarning, tempCategory2);                              // replace the String[] contentWarning in the triggerMap
                // update "contentWarning".txt
                try {
                    FileOutputStream fileOut = new FileOutputStream("TriggerWords/" + contentWarning + ".txt", true);
                    String outputText = ", "+triggerWord;
                    fileOut.write(outputText.getBytes());
                    fileOut.close();
                } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }


            }
            else                                                                                // for the case of nonexistent  "contentWarning" category:
            {
                String[] newTriggerCategory = new String[]{triggerWord};
                triggerMap.put(contentWarning, newTriggerCategory);                             // add "contentWarning" and triggerWord to the Hashmap


                try {                                                                           // create a new "contentWarning".txt file containing "triggerWord"
                    File newCategoryFile = new File("TriggerWords/" + contentWarning + ".txt");
                    newCategoryFile.createNewFile();
                    FileOutputStream fileOut = new FileOutputStream(newCategoryFile);
                    String outputText = triggerWord;
                    fileOut.write(outputText.getBytes());
                    fileOut.close();
                } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
            }

        }
        else
            event.getChannel().sendMessage("Invalid Command").queue();
    }

    void triggerSniffer(MessageReceivedEvent event, String message)
    {
        for(String contentWarning:triggerMap.keySet())  //for each contentWarning category in the map
            for(String word : triggerMap.get(contentWarning))         //for each word in each contentWarning category
            {
                word = " " + word + " ";
                if(Pattern.compile(Pattern.quote(word), Pattern.CASE_INSENSITIVE).matcher(message).find())  //if the message contains a word in a contentWarning category
                {
                    addSpoiler(event, contentWarning, event.getMessageId(), event.getChannel().getId());   //Apply a contentWarning to the post
                    return;
                }
            }
    }

    void viewTriggers(MessageReceivedEvent event, String message)
    {
        //!report -viewTriggers "category"

        String category = trimWord(message, 2);

        ArrayList<String> tempCategory = new ArrayList<String>();
        for (String word : triggerMap.get(category)) tempCategory.add(word);      // add all the words in the category to the temporary arrayList

        if(category  !=  "0")
        {
            event.getChannel().sendMessage("||" + tempCategory.toString() + "||").queue();
        }
        else
            event.getChannel().sendMessage("Invalid Command").queue();
    }

}

