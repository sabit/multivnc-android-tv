/*
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
 * USA.
 */

package com.coboltforge.dontmind.multivnc.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.coboltforge.dontmind.multivnc.Constants;
import com.coboltforge.dontmind.multivnc.R;
import com.coboltforge.dontmind.multivnc.db.ConnectionBean;
import com.coboltforge.dontmind.multivnc.db.VncDatabase;

import java.util.Random;

public class RepeaterAutoConnectActivity extends AppCompatActivity {
    private static final String TAG = "RepeaterAutoConnect";
    private VncDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = VncDatabase.getInstance(this);

        ConnectionBean repeaterConn = database.getConnectionDao().getFirstRepeaterConnection();

        if (repeaterConn == null) {
            Log.d(TAG, "No repeater bookmark found, launching MainMenuActivity");
            Toast.makeText(this, R.string.no_repeater_bookmark, Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainMenuActivity.class));
            finish();
            return;
        }

        createMainMenuShortcut();

        new AlertDialog.Builder(this)
            .setTitle(R.string.repeater_auto_connect_title)
            .setMessage(getString(R.string.quickstart_choice_message))
            .setPositiveButton(R.string.quickstart_start, (dialog, which) -> startQuickstart(repeaterConn))
            .setNegativeButton(R.string.quickstart_open_main, (dialog, which) -> startMainMenu())
            .setCancelable(false)
            .show();
    }

    private void createMainMenuShortcut() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
            if (shortcutManager != null) {
                String shortcutId = "main_menu_shortcut";
                if (shortcutManager.getPinnedShortcuts().stream().noneMatch(s -> s.getId().equals(shortcutId))) {
                    Intent intent = new Intent(this, MainMenuActivity.class);
                    intent.setAction(Intent.ACTION_MAIN);
                    
                    ShortcutInfo shortcut = new ShortcutInfo.Builder(this, shortcutId)
                        .setShortLabel(getString(R.string.app_name))
                        .setLongLabel(getString(R.string.app_name))
                        .setIcon(Icon.createWithResource(this, R.drawable.ic_launcher))
                        .setIntent(intent)
                        .build();
                    
                    shortcutManager.requestPinShortcut(shortcut, null);
                }
            }
        }
    }

    private void startQuickstart(ConnectionBean repeaterConn) {
        String randomRepeaterId = String.valueOf(new Random().nextInt(90000) + 10000);

        ConnectionBean connCopy = new ConnectionBean();
        connCopy.nickname = repeaterConn.nickname;
        connCopy.address = repeaterConn.address;
        connCopy.port = repeaterConn.port;
        connCopy.password = repeaterConn.password;
        connCopy.encodingsString = repeaterConn.encodingsString;
        connCopy.compressModel = repeaterConn.compressModel;
        connCopy.qualityModel = repeaterConn.qualityModel;
        connCopy.colorModel = repeaterConn.colorModel;
        connCopy.forceFull = repeaterConn.forceFull;
        connCopy.repeaterId = randomRepeaterId;
        connCopy.inputMode = repeaterConn.inputMode;
        connCopy.scalemode = repeaterConn.scalemode;
        connCopy.useLocalCursor = repeaterConn.useLocalCursor;
        connCopy.keepPassword = repeaterConn.keepPassword;
        connCopy.followMouse = repeaterConn.followMouse;
        connCopy.useRepeater = true;
        connCopy.metaListId = repeaterConn.metaListId;
        connCopy.lastMetaKeyId = repeaterConn.lastMetaKeyId;
        connCopy.followPan = repeaterConn.followPan;
        connCopy.userName = repeaterConn.userName;
        connCopy.secureConnectionType = repeaterConn.secureConnectionType;
        connCopy.showZoomButtons = repeaterConn.showZoomButtons;
        connCopy.doubleTapAction = repeaterConn.doubleTapAction;
        connCopy.sshHost = repeaterConn.sshHost;
        connCopy.sshPort = repeaterConn.sshPort;
        connCopy.sshUsername = repeaterConn.sshUsername;
        connCopy.sshPassword = repeaterConn.sshPassword;
        connCopy.sshPrivkey = repeaterConn.sshPrivkey;
        connCopy.sshPrivkeyPassword = repeaterConn.sshPrivkeyPassword;
        connCopy.sendExtendedKeys = repeaterConn.sendExtendedKeys;

        Intent intent = new Intent(this, VncCanvasActivity.class);
        intent.putExtra(Constants.CONNECTION, connCopy);
        intent.putExtra(Constants.IS_REPEATER_AUTO_CONNECT, true);
        intent.putExtra(Constants.INITIAL_REPEATER_ID, randomRepeaterId);
        intent.putExtra(Constants.REPEATER_AUTO_RECONNECT, true);
        intent.putExtra(Constants.ORIGINAL_CONNECTION, repeaterConn);

        startActivity(intent);
        finish();
    }

    private void startMainMenu() {
        startActivity(new Intent(this, MainMenuActivity.class));
        finish();
    }
}