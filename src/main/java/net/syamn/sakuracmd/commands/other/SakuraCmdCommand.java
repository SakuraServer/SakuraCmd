/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.other
 * Created: 2013/01/08 15:42:46
 */
package net.syamn.sakuracmd.commands.other;

import java.util.ArrayList;
import java.util.List;

import net.syamn.sakuracmd.SCHelper;
import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.migrator.AdminCmdMigrate;
import net.syamn.sakuracmd.migrator.SakuraServerMigrate;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.storage.I18n;
import net.syamn.sakuracmd.worker.AnnounceWorker;
import net.syamn.utils.LogUtil;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;
import net.syamn.utils.queue.ConfirmQueue;
import net.syamn.utils.queue.Queueable;
import net.syamn.utils.queue.QueuedCommand;

/**
 * SakuraCmdCommand (SakuraCmdCommand.java)
 * @author syam(syamn)
 */
public class SakuraCmdCommand extends BaseCommand implements Queueable{
    public SakuraCmdCommand(){
        bePlayer = false;
        name = "sakuracmd";
        perm = Perms.SAKURACMD;
        argLength = 0;
        usage = "<- admin commands";
    }

    @Override
    public void execute() throws CommandException{
        if (args.size() < 1){
            throw new CommandException("&c引数が足りません！");
        }
        final String func = args.remove(0);

        // reload
        if (func.equalsIgnoreCase("reload")){
            PlayerManager.saveAll();
            Util.message(sender, "&aPlayer data saved!");
            if (args.size() > 0 && args.get(0).equalsIgnoreCase("all")){
                SCHelper.getInstance().reload();
                Util.message(sender, "&aSakuraCmd plugin reloaded!");
            }else{
                try {
                    SCHelper.getInstance().getConfig().loadConfig(false);
                    I18n.setCurrentLanguage(SCHelper.getInstance().getConfig().getLanguage());
                    AnnounceWorker.getInstance().onConfigReload();
                } catch (Exception ex) {
                    if (isPlayer){
                        Util.message(sender, "&cError occred while reloading configuration! Check server console!");
                    }
                    LogUtil.warning("an error occured while trying to load the config file.");
                    ex.printStackTrace();
                    return;
                }
                Util.message(sender, "&aSakuraCmd configuration & locale messages reloaded!");
            }
            return; // reload
        }

        // save
        if (func.equalsIgnoreCase("save")){
            PlayerManager.saveAll(true);
            Util.message(sender, "&aPlayer data force saved!");
            return; // save
        }

        // migrate
        if (func.equalsIgnoreCase("migrate")){
            if (!sender.isOp()){
                throw new CommandException("&cマイグレート実行にはOP権限が必要です！");
            }
            if (args.size() < 1){
                throw new CommandException("&cマイグレート対象のプラグイン名を指定してください!");
            }

            ArrayList<Object> queueArgs = new ArrayList<Object>(2);
            queueArgs.add("migrate");
            queueArgs.add(args.get(0));

            if (args.get(0).equalsIgnoreCase("admincmd")){
                ConfirmQueue.getInstance().addQueue(sender, this, queueArgs, 10);
                Util.message(sender, "&4AdminCmdプラグインからのプレイヤーデータ移行を行います！");
                Util.message(sender, "&4本当に実行しますか？ &a/confirm&4 コマンドで続行します。");
            }
            else if (args.get(0).equalsIgnoreCase("sakuraserver")){
                ConfirmQueue.getInstance().addQueue(sender, this, queueArgs, 10);
                Util.message(sender, "&4SakuraServerプラグインからのプレイヤーデータマージを行います！");
                Util.message(sender, "&4本当に実行しますか？ &a/confirm&4 コマンドで続行します。");
            }
            else{
                throw new CommandException("&cそのプラグインからのマイグレートは未対応です！");
            }
            return; // migrate
        }

        throw new CommandException("&c引数が不正です！");
    }

    @Override
    public void executeQueue(QueuedCommand queued) {
        List<Object> queueArgs = queued.getArgs();
        if (queueArgs.size() == 2){
            if (queueArgs.get(0).equals("migrate")){
                if(((String) queueArgs.get(1)).equalsIgnoreCase("admincmd")){
                    Util.message(sender, "&aマイグレートを開始しました。コンソールを確認してください。");
                    new AdminCmdMigrate(plugin, sender);
                    return;
                }else if(((String) queueArgs.get(1)).equalsIgnoreCase("sakuraserver")){
                    Util.message(sender, "&aマイグレートを開始しました。コンソールを確認してください。");
                    new SakuraServerMigrate(plugin, sender);
                    return;
                }
            }
        }
    }
}