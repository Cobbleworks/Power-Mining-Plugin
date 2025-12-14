package de.andidoescode.powermining;

import de.andidoescode.powermining.commands.GiveMagnetHopperCommand;
import de.andidoescode.powermining.commands.GiveOreScannerBellCommand;
import de.andidoescode.powermining.listeners.MountedMiningListener;
import de.andidoescode.powermining.managers.MagnetHopperManager;
import de.andidoescode.powermining.managers.OreScannerBellManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PowerMining extends JavaPlugin {

    private static PowerMining instance;
    
    private MagnetHopperManager magnetHopperManager;
    private OreScannerBellManager oreScannerBellManager;

    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        magnetHopperManager = new MagnetHopperManager(this);
        oreScannerBellManager = new OreScannerBellManager(this);
        
        registerListeners();
        registerCommands();
        
        getLogger().info("Power Mining has been enabled!");
    }

    @Override
    public void onDisable() {
        if (magnetHopperManager != null) {
            magnetHopperManager.shutdown();
        }
        if (oreScannerBellManager != null) {
            oreScannerBellManager.shutdown();
        }
        
        getLogger().info("Power Mining has been disabled!");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new MountedMiningListener(this), this);
        getServer().getPluginManager().registerEvents(magnetHopperManager, this);
        getServer().getPluginManager().registerEvents(oreScannerBellManager, this);
    }

    private void registerCommands() {
        var giveMagnetHopperCommand = getCommand("givemagnethopper");
        if (giveMagnetHopperCommand != null) {
            giveMagnetHopperCommand.setExecutor(new GiveMagnetHopperCommand(this));
        }
        
        var giveOreScannerBellCommand = getCommand("giveorescannerbell");
        if (giveOreScannerBellCommand != null) {
            giveOreScannerBellCommand.setExecutor(new GiveOreScannerBellCommand(this));
        }
    }

    public static PowerMining getInstance() {
        return instance;
    }

    public MagnetHopperManager getMagnetHopperManager() {
        return magnetHopperManager;
    }

    public OreScannerBellManager getOreScannerBellManager() {
        return oreScannerBellManager;
    }
}
