package com.loops101.ui;

import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.CheckinMetaHandler;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.loops101.CodestyleHookProcessor;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class CodestyleBeforeCheckinHandler extends CheckinHandler implements CheckinMetaHandler {

    private final Project myProject;
    private boolean applyCodeStyle = true;
    private final CheckinProjectPanel myPanel;


    public CodestyleBeforeCheckinHandler(final Project project, final CheckinProjectPanel panel) {
        myProject = project;
        myPanel = panel;
    }


    @Override
    @Nullable
    public RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
        final JCheckBox codestyleBox = new JCheckBox("Apply coding style");

        return new RefreshableOnComponent() {
            @Override
            public JComponent getComponent() {
                final JPanel panel = new JPanel(new GridLayout(1, 0));
                panel.add(codestyleBox);
                return panel;
            }

            @Override
            public void refresh() {
            }

            @Override
            public void saveState() {
                applyCodeStyle = codestyleBox.isSelected();
            }

            @Override
            public void restoreState() {
                codestyleBox.setSelected(applyCodeStyle);
            }
        };

    }

    @Override
    public void runCheckinHandlers(final Runnable finishAction) {
        final Runnable performCheckoutAction = new Runnable() {
            @Override
            public void run() {
                FileDocumentManager.getInstance().saveAllDocuments();
                finishAction.run();
            }
        };

        if (applyCodeStyle) {
            final Collection<VirtualFile> files = myPanel.getVirtualFiles();
            CodestyleHookProcessor.reformat(myProject, files, performCheckoutAction);
        } else {
            performCheckoutAction.run();
        }
    }

}