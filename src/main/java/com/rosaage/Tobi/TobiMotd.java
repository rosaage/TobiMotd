package com.rosaage.Tobi;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import me.escapeNT.pail.Pail;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.JTextField;
import javax.swing.JCheckBox;
 
public class TobiMotd extends JavaPlugin implements Listener {
//TOBIGUI
private JTextArea textArea;
private JTextArea textArea_1;
private JTextArea TA_1;
private JPanel panel;
private JLabel jLabel1;
private JTextField textField;
private JTextField textField_1;
private JTextField textField_2;
private JCheckBox chckbxOutputToConsole;
private JCheckBox chckbxTimerEnabled;
private FileConfiguration config = getConfig();
private String Enabled = "[TobiMotd] v2.8 is enabled!";
private String Disabled = "[TobiMotd] v2.8 is disabled!";

public void onDisable() {
	System.out.println(Disabled);
}
public void onEnable() {
config = this.getConfig();
if(!config.contains("MOTD") && !config.contains("Timer.Timer Enabled")){
	config.addDefault("MOTD", "this is my motd");
	config.addDefault("Timer.Timer Enabled", false);
	config.addDefault("Timer.Timer Minutes", 15);
	config.addDefault("Timer.Timer Seconds", 0);
	config.addDefault("Timer.Timer Ticks", 0);
	config.addDefault("Timer.Output to console", false);
	List<String> configList = Arrays.asList("Hello World", "Welcome to Bukkit", "Have a Good Day!");
	config.addDefault("Timer.Timer Message", configList);
}

config.options().copyDefaults(true);
saveConfig();
checkifpail();
Starttimer();
getServer().getPluginManager().registerEvents(this, this);
getCommand("motd").setExecutor(new CommandExecutor(){
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("motd")){
			
			World world = sender instanceof Player ? ((Player) sender).getWorld() : getServer().getWorlds().get(0);
			long time = world.getTime();
			int hours = (int) ((time / 1000+8) % 24);
			int minutes = (int) (60 * (time % 1000) / 1000);
			String[] message = config.getString("MOTD").split("%NL%");
			for(int x=0 ; x<message.length ; x++) {
			sender.sendMessage(message[x].replaceAll("(&([a-f0-9]))", "\u00A7$2").replaceAll("%NAME%", sender.getName())
					.replaceAll("%TIME%", hours + ":" + minutes).replaceAll("%VERSION%", getServer().getBukkitVersion())
					.replaceAll("%WORLD%", world.getName()).replaceAll("%ONLINE%", list(sender)).replaceAll("%I%", ChatColor.ITALIC + "")
					.replaceAll("%B%", ChatColor.BOLD + "").replaceAll("%ST%", ChatColor.STRIKETHROUGH + "").replaceAll("%M%", ChatColor.MAGIC + "")
					.replaceAll("%UL%", ChatColor.UNDERLINE + "").replaceAll("%D%", ChatColor.RESET + "")
					.replaceAll("%PAMOUNT%", Integer.toString(Bukkit.getOnlinePlayers().length)));
			}
			return true;
		}
		return false; 
	}
});
getCommand("setmotd").setExecutor(new CommandExecutor(){
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("setmotd")){
			if(sender.hasPermission("tobimotd.set") || sender.isOp()) {
				StringBuilder sb = new StringBuilder();
				for (String arg : args)
				sb.append(arg + " ");
				config.set("MOTD", sb.toString());
				saveConfig();
				reloadConfig();
				config = getConfig();
				World world = sender instanceof Player ? ((Player) sender).getWorld() : getServer().getWorlds().get(0);
				long time = world.getTime();
				int hours = (int) ((time / 1000+8) % 24);
				int minutes = (int) (60 * (time % 1000) / 1000);
				String[] message = config.getString("MOTD").split("%NL%");
				for(int x=0 ; x<message.length ; x++) {
				sender.sendMessage("The motd is now: " + message[x].replaceAll("(&([a-f0-9]))", "\u00A7$2").replaceAll("%NAME%", sender.getName())
						.replaceAll("%TIME%", hours + ":" + minutes).replaceAll("%VERSION%", getServer().getBukkitVersion())
						.replaceAll("%WORLD%", world.getName()).replaceAll("%ONLINE%", list(sender)).replaceAll("%I%", ChatColor.ITALIC + "")
						.replaceAll("%B%", ChatColor.BOLD + "").replaceAll("%ST%", ChatColor.STRIKETHROUGH + "").replaceAll("%M%", ChatColor.MAGIC + "")
						.replaceAll("%UL%", ChatColor.UNDERLINE + "").replaceAll("%D%", ChatColor.RESET + "")
						.replaceAll("%PAMOUNT%", Integer.toString(Bukkit.getOnlinePlayers().length)));
					}
				return true;
			}
			return false;
		}
	return false;
	}
});
getCommand("tm").setExecutor(new CommandExecutor(){
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("tm")){
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("reload")) {
	            	if(sender.hasPermission("tobimodt.reload") || sender.isOp()){
	            		reloadConfig();
	            		config = getConfig();
	    				saveConfig();
	    			    RestartTimer();
	    				sender.sendMessage(ChatColor.DARK_AQUA + "Config reloaded!");
	    				return true;
	    			} else{
	    				sender.sendMessage(ChatColor.RED + "You need the permission tobimotd.reload or be op to use this command!");
	    				return false;
	    			}
	            } else if (args[0].equalsIgnoreCase("timer")){
	            	if(sender.hasPermission("tobimodt.timer") || sender.isOp()){
						if (args.length >= 2) {
							if (args[1].equalsIgnoreCase("on")) {
								config.set("Timer.Timer Enabled", true);
								saveConfig();
							}else if (args[1].equalsIgnoreCase("off")){
								config.set("Timer.Timer Enabled", false);
								saveConfig();
							}else if (args[1].equalsIgnoreCase("add")) {
								StringBuilder sb = new StringBuilder();
								for (String arg : args)
								sb.append(arg + " ");
								List<String> message = config.getStringList("Timer.Timer Message");
								message.add(sb.toString().substring(10));
								config.set("Timer.Timer Message", message);
								saveConfig();
							} else if (args[1].equalsIgnoreCase("list")){
								for (int T = 0; T < config.getList("Timer.Timer Message").size(); T++) {
							    	sender.sendMessage( T + " - " + (String) config.getList("Timer.Timer Message").get(T));
							    }
							} else if (args[1].equalsIgnoreCase("remove")){
								if (args.length >= 3) {
									List<String> message = config.getStringList("Timer.Timer Message");
						    		if ((Integer.parseInt(args[2]) >= 0) && (Integer.parseInt(args[2]) < message.size()) && (message.size() != 0)) {
						    			message.remove(Integer.parseInt(args[2]));
						    			config.set("Timer.Timer Message", message);
						    			saveConfig();
						    			sender.sendMessage("Removed: " + message.get(Integer.parseInt(args[2])));
						    		} else if (Integer.parseInt(args[2]) >= message.size()) {
						    			sender.sendMessage(ChatColor.RED + "Number is too high!");
						    		} else {
						    			sender.sendMessage(ChatColor.RED + "Wrong number, try again");
						    		}
								}
							} else if (args[1].equalsIgnoreCase("set")){
								if (args.length >= 3) {
									if (args.length >= 4) {
										if (args.length >= 5) {
											config.set("Timer.Timer Minutes", Integer.parseInt(args[2]));
							            	config.set("Timer.Timer Seconds", Integer.parseInt(args[3]));
							            	config.set("Timer.Timer Ticks", Integer.parseInt(args[4]));
							            	saveConfig();
						        			return true;
						        		}
										config.set("Timer.Timer Minutes", Integer.parseInt(args[2]));
						            	config.set("Timer.Timer Seconds", Integer.parseInt(args[3]));
						            	saveConfig();
						    			return true;
						    		}
									config.set("Timer.Timer Minutes", Integer.parseInt(args[4]));
									saveConfig();
									return true;
								}
							}
							RestartTimer();
							return true;
						}
					} else{
	    				sender.sendMessage(ChatColor.RED + "You need the permission tobimotd.timer or be op to use this command!");
	    				return false;
	    			}
	            } else if (args[0].equals("help")) {
	            	sender.sendMessage(
	            	ChatColor.BOLD + "" + ChatColor.BLUE + "Commands:\n"+
	            	ChatColor.RED + "/tm reload\n" + ChatColor.RESET +
	            	"Reloads the config file if you have edited it manually\n"+
	            	ChatColor.RED + "/tm timer on|off\n"+ ChatColor.RESET +
	            	"Turns the timer on or off\n"+
	            	ChatColor.RED + "/tm timer add (Message)\n"+ ChatColor.RESET +
	            	"Adds (Message) to the end of the Timer Messages list\n"+
	            	ChatColor.RED + "/tm timer list\n"+ ChatColor.RESET +
	            	"This lists all the sentences in the timer message list\nYou need the numbers next to them when using the command below\n"+
	            	ChatColor.RED + "/tm timer remove (number)\n"+ ChatColor.RESET +
	            	"removes line (number) from the message list\nuse the command above to get the number\n"+
	            	ChatColor.RED + "/tm timer set (min) [(sec) (tick)]\n"+ ChatColor.RESET +
	            	"Sets the timer interval, [] is optional"
	            	);
	            	return true;
	            }
			}
			if(sender.hasPermission("tobimodt.reload") || sender.isOp()){
        		reloadConfig();
        		config = getConfig();
				saveConfig();
			    RestartTimer();
				sender.sendMessage(ChatColor.DARK_AQUA + "Config reloaded!");
				return true;
			} else{
				sender.sendMessage(ChatColor.RED + "You need the permission tobimotd.reload or be op to use this command!");
				return false;
			}
		}
		sender.sendMessage(ChatColor.RED + "ERROR " + new Exception().getStackTrace()[0].getLineNumber() + "!");
		return false;
	}
});
getCommand("TobiGUI").setExecutor(new CommandExecutor(){
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("TobiGUI")){
			if(sender.hasPermission("tobimodt.GUI") || sender.isOp()){
				
				TobiGUI();
				
			return true;
			}
			else{
				sender.sendMessage(ChatColor.RED + "You need the permission tobimotd.TobiGUI or be op to use this command!");
				return false;
			}
		}
		sender.sendMessage(ChatColor.RED + "ERROR " + new Exception().getStackTrace()[0].getLineNumber() + "!");
		return false;
	}
});
System.out.println(Enabled);
}
public void updateG() {
	TA_1.setText("");
    for (int T = 0; T < config.getList("Timer.Timer Message").size(); T++) {
    	TA_1.append(config.getList("Timer.Timer Message").get(T) + "\n");
    }
    if(config.getBoolean("Timer.Output to console")){
    	chckbxOutputToConsole.setSelected(true);
    }
    if(config.getBoolean("Timer.Timer Enabled")){
    	chckbxTimerEnabled.setSelected(true);
    }
    textArea_1.setText(config.getString("MOTD"));
    textArea.setText(config.getString("MOTD"));
    textField.setText(Integer.toString(config.getInt("Timer.Timer Minutes")));
    textField_1.setText(Integer.toString(config.getInt("Timer.Timer Seconds")));
    textField_2.setText(Integer.toString(config.getInt("Timer.Timer Ticks")));
    RestartTimer();
}


public JPanel panelG(){
	//panel
	panel = new JPanel();
    panel.setLayout(null);
    panel.setLocation(new Point(150, 150));
    
    //scroll pane, textarea
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(10, 11, 465, 114);
    panel.add(scrollPane);
    textArea = new JTextArea();
    scrollPane.setViewportView(textArea);
    textArea.setText(config.getString("MOTD"));
    jLabel1 = new JLabel("Set the Motd");
    scrollPane.setColumnHeaderView(jLabel1);
    
    //scroll area 1 + text area 1
    JScrollPane scrollPane_1 = new JScrollPane();
    scrollPane_1.setBounds(10, 136, 465, 114);
    panel.add(scrollPane_1);
    textArea_1 = new JTextArea();
    textArea_1.setEditable(false);
    scrollPane_1.setViewportView(textArea_1);
    textArea_1.setText(config.getString("MOTD"));
    JLabel lblCurrentMotd = new JLabel("Current Motd:");
    scrollPane_1.setColumnHeaderView(lblCurrentMotd);
    
    //save button
    JButton btnSave = new JButton("Save All");
    btnSave.setBounds(210, 308, 120, 23);
    panel.add(btnSave);
    //CheckBox
    chckbxOutputToConsole = new JCheckBox("Output to console");
    if(config.getBoolean("Timer.Output to console")){
    	chckbxOutputToConsole.setSelected(true);
    }
    chckbxOutputToConsole.setBounds(210, 257, 277, 23);
    panel.add(chckbxOutputToConsole);
    //CheckBox
    chckbxTimerEnabled = new JCheckBox("Timer Enabled");
    if(config.getBoolean("Timer.Timer Enabled")){
    	chckbxTimerEnabled.setSelected(true);
    }
    chckbxTimerEnabled.setBounds(210, 283, 277, 23);
    panel.add(chckbxTimerEnabled);
    // save button action
    btnSave.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent event) {
            	config.set("MOTD", textArea.getText());
            	config.set("Timer.Timer Minutes", Integer.parseInt(textField.getText()));
            	config.set("Timer.Timer Seconds", Integer.parseInt(textField_1.getText()));
            	config.set("Timer.Timer Ticks", Integer.parseInt(textField_2.getText()));
            	if (chckbxTimerEnabled.isSelected()) {
            		config.set("Timer.Timer Enabled", true);
            	}else{
            		config.set("Timer.Timer Enabled", false);
            	}
            	if (chckbxOutputToConsole.isSelected()) {
            		config.set("Timer.Output to console", true);
            	}else{
            		config.set("Timer.Output to console", false);
            	}
            	config.set("Timer.Timer Message", TA_1.getText().split("\\n"));
            	saveConfig();
				reloadConfig();
				updateG();
    		}
    });
    //label
    JLabel lblTimerMinuettes = new JLabel("Timer Minuetes:");
    lblTimerMinuettes.setBounds(10, 261, 110, 14);
    panel.add(lblTimerMinuettes);
    //label
    JLabel lblTimerSeconds = new JLabel("Timer Seconds:");
    lblTimerSeconds.setBounds(10, 287, 110, 14);
    panel.add(lblTimerSeconds);
    //label
    JLabel lblTimerTicks = new JLabel("Timer Ticks:");
    lblTimerTicks.setBounds(10, 312, 110, 14);
    panel.add(lblTimerTicks);
    //textfield
    textField = new JTextField();
    textField.setText(Integer.toString(config.getInt("Timer.Timer Minutes")));
    textField.setBounds(130, 258, 60, 20);
    textField.setColumns(10);
    panel.add(textField);
    //textfield1
    textField_1 = new JTextField();
    textField_1.setText(Integer.toString(config.getInt("Timer.Timer Seconds")));
    textField_1.setBounds(130, 284, 60, 20);
    textField_1.setColumns(10);
    panel.add(textField_1);
    //textfield2
    textField_2 = new JTextField();
    textField_2.setText(Integer.toString(config.getInt("Timer.Timer Ticks")));
    textField_2.setBounds(130, 309, 60, 20);
    textField_2.setColumns(10);
    panel.add(textField_2);
    //scrollpane 2 and TA_1
    JScrollPane scrollPane_2 = new JScrollPane();
    scrollPane_2.setBounds(10, 337, 465, 114);
    TA_1 = new JTextArea();
    scrollPane_2.setViewportView(TA_1);
    panel.add(scrollPane_2);
    TA_1.setText("");
    for (int T = 0; T < config.getList("Timer.Timer Message").size(); T++) {
    	TA_1.append(config.getList("Timer.Timer Message").get(T) + "\n");
    }
    //scrollpane labellabel
    JLabel lblTimerMessages = new JLabel("Timer Messages:");
    scrollPane_2.setColumnHeaderView(lblTimerMessages);
    //update Gui btn
    JButton btnUpdateGui = new JButton("Update GUI");
    btnUpdateGui.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		updateG();
    	}
    });
    btnUpdateGui.setBounds(340, 308, 110, 23);
    panel.add(btnUpdateGui);
    
    //return the panel
	return panel;
}

/**
 * @wbp.parser.entryPoint
 */
public void TobiGUI() {
	final JFrame frame = new JFrame("TobiGUI");
	frame.setBounds(0, 0, 500, 500);
    frame.getContentPane().add(panelG());
    frame.setVisible(true);
}

String on () {
	StringBuilder players = new StringBuilder();

	for (Player player : Bukkit.getOnlinePlayers()) {
		if (players.length() > 0) {
			players.append(", ");
		}
		players.append(player.getDisplayName());
	}
	return players.toString();
}
private void RestartTimer() {
	final FileConfiguration config = this.getConfig();
	if(config.getBoolean("Timer.Timer Enabled") == true) {
    	getServer().getScheduler().cancelTasks(this);
    	Starttimer();
    } else {
    	getServer().getScheduler().cancelTasks(this);
    }
}
private void Starttimer() {
	final FileConfiguration config = this.getConfig();
	if (config.getBoolean("Timer.Timer Enabled") == true) {
		int Time = config.getInt("Timer.Timer Minutes")*60*20 + config.getInt("Timer.Timer Seconds")*20 + config.getInt("Timer.Timer Ticks");
		long timee = Time;
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			 
			  public void run() {
		    	  List<String> message = config.getStringList("Timer.Timer Message");
		    	  int random = (int )(Math.random() * message.size());
		    	  String messagee = message.get(random);
		    	  String[] messag = messagee.split("%NL%");
				  if (config.getBoolean("Timer.Output to console") == true){
					  World world = getServer().getWorlds().get(0);
					  long time = world.getTime();
				      int hours = (int) ((time / 1000+8) % 24);
				      int minutes = (int) (60 * (time % 1000) / 1000);
					  for(int x=0 ; x<messag.length ; x++) {
						getServer().getConsoleSender().sendMessage(messag[x].replaceAll("(&([a-f0-9]))", "\u00A7$2").replaceAll("%NAME%", getServer().getConsoleSender().getName())
									.replaceAll("%TIME%", hours + ":" + minutes).replaceAll("%VERSION%",getServer().getBukkitVersion())
									.replaceAll("%WORLD%", world.getName()).replaceAll("%ONLINE%", on()).replaceAll("%I%", ChatColor.ITALIC + "")
									.replaceAll("%B%", ChatColor.BOLD + "").replaceAll("%ST%", ChatColor.STRIKETHROUGH + "").replaceAll("%M%", ChatColor.MAGIC + "")
									.replaceAll("%UL%", ChatColor.UNDERLINE + "").replaceAll("%D%", ChatColor.RESET + "")
									.replaceAll("%PAMOUNT%", Integer.toString(Bukkit.getOnlinePlayers().length)));
						}
				  }
				  for (Player p : getServer().getOnlinePlayers()) {
			    		World world = p instanceof Player ? ((Player) p).getWorld() : getServer().getWorlds().get(0);
			    		long time = world.getTime();
				    	int hours = (int) ((time / 1000+8) % 24);
				    	int minutes = (int) (60 * (time % 1000) / 1000);
						for(int x=0 ; x<messag.length ; x++) {
						p.sendMessage(messag[x].replaceAll("(&([a-f0-9]))", "\u00A7$2").replaceAll("%NAME%", p.getName())
								.replaceAll("%TIME%", hours + ":" + minutes).replaceAll("%VERSION%",getServer().getBukkitVersion())
								.replaceAll("%WORLD%", world.getName()).replaceAll("%ONLINE%", list(p)).replaceAll("%I%", ChatColor.ITALIC + "")
								.replaceAll("%B%", ChatColor.BOLD + "").replaceAll("%ST%", ChatColor.STRIKETHROUGH + "").replaceAll("%M%", ChatColor.MAGIC + "")
								.replaceAll("%UL%", ChatColor.UNDERLINE + "").replaceAll("%D%", ChatColor.RESET + "")
								.replaceAll("%PAMOUNT%", Integer.toString(Bukkit.getOnlinePlayers().length)));
							}
					}
			  }
			},60L, timee);
	}
}
@EventHandler
public void PlayerJoin(PlayerJoinEvent event) {
	final FileConfiguration config = this.getConfig();
	World world = event.getPlayer() instanceof Player ? ((Player) event.getPlayer()).getWorld() : getServer().getWorlds().get(0);
	long time = world.getTime();
	Player p = event.getPlayer();
	CommandSender sender = (Player) p;
	int hours = (int) ((time / 1000+8) % 24);
	int minutes = (int) (60 * (time % 1000) / 1000);
	String[] message = config.getString("MOTD").split("%NL%");
	for(int x=0 ; x<message.length ; x++) {
	p.sendMessage(message[x].replaceAll("(&([a-f0-9]))", "\u00A7$2").replaceAll("%NAME%", p.getName())
			.replaceAll("%TIME%", hours + ":" + minutes).replaceAll("%VERSION%", getServer().getBukkitVersion())
			.replaceAll("%WORLD%", world.getName()).replaceAll("%ONLINE%", list(sender)).replaceAll("%I%", ChatColor.ITALIC + "")
			.replaceAll("%B%", ChatColor.BOLD + "").replaceAll("%ST%", ChatColor.STRIKETHROUGH + "").replaceAll("%M%", ChatColor.MAGIC + "")
			.replaceAll("%UL%", ChatColor.UNDERLINE + "").replaceAll("%D%", ChatColor.RESET + "")
			.replaceAll("%PAMOUNT%", Integer.toString(Bukkit.getOnlinePlayers().length)));
		}
 }
String list (CommandSender sender) {
	StringBuilder players = new StringBuilder();

	for (Player player : Bukkit.getOnlinePlayers()) {
		if (sender instanceof Player && !((Player) sender).canSee(player))
			continue;
		if (players.length() > 0) {
			players.append(", ");
		}
		players.append(player.getDisplayName());
	}
	return players.toString();
}

private void checkifpail() {
    final PluginManager pm = getServer().getPluginManager();
    if (pm.isPluginEnabled("Pail")) {
        ((Pail)pm.getPlugin("Pail")).loadInterfaceComponent("TobiMotd", panelG());
        System.out.println("[TobiMotd] " + "Done!");
    }
    else {
        System.out.println("[TobiMotd] Pail isn't loaded");
        System.out.println("[TobiGUI]  Starting TobiGUI, Type: 'TobiGUI' to start it");
    }
}
}