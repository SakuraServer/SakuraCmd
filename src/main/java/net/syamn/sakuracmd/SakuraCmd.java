/**
 * SakuraCmd - Package: net.syamn.sakuracmd
 * Created: 2012/12/28 13:35:03
 */
package net.syamn.sakuracmd;

import java.io.IOException;
import java.util.List;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.syamn.sakuracmd.commands.CommandHandler;
import net.syamn.sakuracmd.commands.CommandRegister;
import net.syamn.sakuracmd.listener.EntityListener;
import net.syamn.sakuracmd.listener.PlayerListener;
import net.syamn.sakuracmd.manager.ServerManager;
import net.syamn.sakuracmd.permission.PermissionManager;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.utils.plugin.DynmapHandler;
import net.syamn.sakuracmd.worker.AFKWorker;
import net.syamn.utils.LogUtil;
import net.syamn.utils.Metrics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * SakuraCmd (SakuraCmd.java)
 * @author syam(syamn)
 */
public class SakuraCmd extends JavaPlugin{
    // ** Logger **
    public final static String logPrefix = "[SakuraCmd] ";
    public final static String msgPrefix = "&c[SakuraCmd] &f";

    // ** Listener **
    // ServerListener serverListener = new ServerListener(this);

    // ** Commands **
    private CommandHandler commandHandler;
    
    // ** Managers **
    private ServerManager serverMan;

    // ** Private Classes **
    private SCHelper worker;
    

    // ** Static **
    //private static Database database;

    // ** Instance **
    private static SakuraCmd instance;

    // ** Hookup Plugins **
    //private static Vault vault = null;
    //private static Economy economy = null;

    /**
     * プラグイン起動処理
     */
    @Override
    public void onEnable() {
        instance = this;
        LogUtil.init(this);
        
        worker = SCHelper.getInstance();
        worker.setMainPlugin(this);

        // Managers
        serverMan = new ServerManager(this);
        
        // Regist Listeners
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new EntityListener(this), this);

        // commands
        commandHandler = new CommandHandler(this);
        CommandRegister.registerCommands(commandHandler);

        // メッセージ表示
        PluginDescriptionFile pdfFile = this.getDescription();
        LogUtil.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");

        setupMetrics(); // mcstats
    }

    /**
     * プラグイン停止処理
     */
    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        
        // dispose all components
        AFKWorker.dispose();
        SCHelper.dispose();
        
        // Save player profiles
        PlayerManager.saveAll();
        
        // disable dynmap hook
        if (DynmapHandler.getInstance().isActivated()){
            DynmapHandler.getInstance().deactivate();
        }
        DynmapHandler.dispose();

        // メッセージ表示
        PluginDescriptionFile pdfFile = this.getDescription();
        LogUtil.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!");
    }
    
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        return commandHandler.onCommand(sender, command, label, args);
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return commandHandler.onTabComplete(sender, command, alias, args);
    }

    /**
     * Vaultプラグインにフック
     */
    /*
    public boolean setupVault() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("Vault");
        if (plugin != null & plugin instanceof Vault) {
            RegisteredServiceProvider<Economy> economyProvider = getServer()
                    .getServicesManager().getRegistration(
                            net.milkbowl.vault.economy.Economy.class);
            // 経済概念のプラグインがロードされているかチェック
            if (economyProvider == null) {
                log.warning(logPrefix
                        + "Economy plugin NOT found. Disabled Vault plugin integration.");
                return false;
            }

            try {
                vault = (Vault) plugin;
                economy = economyProvider.getProvider();

                if (vault == null || economy == null) {
                    throw new NullPointerException();
                }
            } // 例外チェック
            catch (Exception e) {
                log.warning(logPrefix
                        + "Could NOT be hook to Vault plugin. Disabled Vault plugin integration.");
                return false;
            }

            // Success
            log.info(logPrefix + "Hooked to Vault plugin!");
            return true;
        } else {
            // Vaultが見つからなかった
            log.warning(logPrefix
                    + "Vault plugin was NOT found! Disabled Vault integration.");
    */
    
    /**
     * サーバマネージャを返す
     * @return
     */
    public ServerManager getServerManager(){
        return this.serverMan;
    }

    /**
     * Metricsセットアップ
     */
    private void setupMetrics() {
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException ex) {
            LogUtil.warning(logPrefix + "cant send metrics data!");
            ex.printStackTrace();
        }
    }

    /* getter */
    /**
     * SCHelperインスタンスを返す
     * @return SCHelper
     */
    public SCHelper getWorker(){
        return this.worker;
    }

    /**
     * インスタンスを返す
     *
     * @return SakuraCmdインスタンス
     */
    public static SakuraCmd getInstance() {
        return instance;
    }
}
