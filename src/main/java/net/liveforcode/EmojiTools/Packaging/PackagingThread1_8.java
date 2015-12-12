/*
 * Emoji Tools helps users and developers of Android, iOS, and OS X extract, modify, and repackage Emoji fonts.
 * Copyright (C) 2015 Mitch Talmadge
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact Mitch Talmadge at mitcht@liveforcode.net
 */

package net.liveforcode.EmojiTools.Packaging;

import net.liveforcode.EmojiTools.ConsoleManager;
import net.liveforcode.EmojiTools.EmojiTools;
import net.liveforcode.EmojiTools.GUI.EmojiToolsGUI;
import net.liveforcode.EmojiTools.GUI.PackagingDialog;
import net.liveforcode.EmojiTools.GUI.Tabs.PackagingTab;
import net.liveforcode.EmojiTools.JythonHandler;
import org.python.core.PyList;
import org.python.core.PyType;

import java.io.File;
import java.util.ArrayList;

class PackagingThread1_8 extends Thread implements ConsoleManager.ConsoleListener, EmojiTools.JythonListener {

    private final EmojiToolsGUI gui;
    private final File pngDirectory;
    private final PackagingManager packagingManager;
    private final PackagingDialog packagingDialog;
    private final int outputType;
    private boolean running = true;

    public PackagingThread1_8(EmojiToolsGUI gui, File pngDirectory, PackagingManager packagingManager, PackagingDialog packagingDialog, int outputType) {
        super("PackagingThread");
        this.gui = gui;
        this.pngDirectory = pngDirectory;
        this.packagingManager = packagingManager;
        this.packagingDialog = packagingDialog;
        this.outputType = outputType;
    }

    @Override
    public void run() {
        if (outputType == PackagingTab.ANDROID) { //TODO: Implement iOS and OSX Emoji Fonts
            gui.getConsoleManager().addConsoleListener(this);

            packagingDialog.setIndeterminate(true);
            packagingDialog.appendToStatus("Compiling Scripts... (This can take a minute. Please Wait...)");

            EmojiTools.addJythonListener(this);
        }
    }

    public void endPackaging() {
        running = false;
    }

    @Override
    public void write(String message) {
        this.packagingDialog.writeToStatus(message);
    }

    @Override
    public void onJythonReady(JythonHandler jythonHandler) {
        try {
            File outputDirectory = new File(EmojiTools.getRootDirectory(), "Output");
            if (!outputDirectory.exists())
                outputDirectory.mkdir();

            packagingDialog.setIndeterminate(false);

            packagingDialog.appendToStatus("Extracting Scripts...");

            //---- add_glyphs.py ----//

            packagingDialog.setProgress(25);

            packagingDialog.appendToStatus("Running add_glyphs.py...");

            //Set sys.argv
            String fontTemplatePath;
            fontTemplatePath = jythonHandler.getScriptDirectory().getAbsolutePath() + "/FontTemplates/NotoColorEmoji.tmpl.ttx";

            ArrayList<String> argvList = new ArrayList<>();
            argvList.add("add_glyphs.py");                                      //Python Script Name
            argvList.add(fontTemplatePath);                                     //Template Path
            argvList.add(jythonHandler.getScriptDirectory().getAbsolutePath() + "/NotoColorEmoji.ttx"); //Output ttx path
            argvList.add(pngDirectory.getAbsolutePath() + "/uni");              //Prefix Path

            jythonHandler.getPySystemState().argv = new PyList(PyType.fromClass(String.class), argvList);

            if (!running)
                return;

            //Execute
            jythonHandler.getPythonInterpreter().execfile(jythonHandler.getScriptDirectory().getAbsolutePath() + "/PythonScripts/add_glyphs.py");

            //---- package.py ----//

            packagingDialog.setProgress(50);

            packagingDialog.appendToStatus("Running package.py...");

            //Set sys.argv
            argvList = new ArrayList<>();
            argvList.add("package.py");                                                 //Python Script Name
            argvList.add("-o");                                                         //Output flag
            argvList.add(jythonHandler.getScriptDirectory().getAbsolutePath() + "/NotoColorEmoji.empty.ttf");   //Output empty ttf path
            argvList.add(jythonHandler.getScriptDirectory().getAbsolutePath() + "/NotoColorEmoji.ttx");         //ttx path

            jythonHandler.getPySystemState().argv = new PyList(PyType.fromClass(String.class), argvList);

            if (!running)
                return;

            //Execute
            jythonHandler.getPythonInterpreter().execfile(jythonHandler.getScriptDirectory().getAbsolutePath() + "/PythonScripts/package.py");

            //---- emoji_builder.py.py ----//

            packagingDialog.setProgress(75);

            packagingDialog.appendToStatus("Running emoji_builder.py...");

            //Set sys.argv
            argvList = new ArrayList<>();
            argvList.add("emoji_builder.py");                                           //Python Script Name
            argvList.add(jythonHandler.getScriptDirectory().getAbsolutePath() + "/NotoColorEmoji.empty.ttf");   //Empty ttf path
            argvList.add(outputDirectory.getAbsolutePath() + "/NotoColorEmoji.ttf");    //Output ttf path
            argvList.add(pngDirectory.getAbsolutePath() + "/uni");                      //Prefix Path

            jythonHandler.getPySystemState().argv = new PyList(PyType.fromClass(String.class), argvList);

            if (!running)
                return;

            //Execute
            jythonHandler.getPythonInterpreter().execfile(jythonHandler.getScriptDirectory().getAbsolutePath() + "/PythonScripts/emoji_builder.py");

            packagingDialog.setProgress(100);

        } catch (Exception e) {
            EmojiTools.submitError(Thread.currentThread(), e);
        } finally {
            gui.getConsoleManager().removeConsoleListener(this);

            packagingDialog.dispose();
        }
    }
}
