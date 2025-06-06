/*
 * MegaMekLab - Copyright (C) 2009
 *
 * Original author - jtighe (torren@users.sourceforge.net)
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */
package megameklab.ui.combatVehicle;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import megamek.common.Entity;
import megamek.common.MekFileParser;
import megamek.common.Mounted;
import megamek.common.loaders.EntityLoadingException;
import megamek.logging.MMLogger;
import megameklab.ui.EntitySource;
import megameklab.ui.generalUnit.UnallocatedView;
import megameklab.ui.util.ITab;
import megameklab.ui.util.RefreshListener;
import megameklab.util.UnitUtil;

public class CVBuildTab extends ITab implements ActionListener {
    private static final MMLogger logger = MMLogger.create(CVBuildTab.class);

    private RefreshListener refresh = null;
    private CVCriticalView critView;

    public UnallocatedView getUnallocatedView() {
        return unallocatedView;
    }

    private UnallocatedView unallocatedView;

    private JButton autoFillButton = new JButton("Auto Fill");
    private JButton resetButton = new JButton("Reset");

    private String AUTOFILLCOMMAND = "autofillbuttoncommand";
    private String RESETCOMMAND = "resetbuttoncommand";

    public CVBuildTab(EntitySource eSource) {
        super(eSource);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        critView = new CVCriticalView(eSource, refresh);
        unallocatedView = new UnallocatedView(eSource, () -> refresh);

        mainPanel.add(unallocatedView);

        autoFillButton.setMnemonic('A');
        autoFillButton.setActionCommand(AUTOFILLCOMMAND);
        resetButton.setMnemonic('R');
        resetButton.setActionCommand(RESETCOMMAND);
        buttonPanel.add(autoFillButton);
        buttonPanel.add(resetButton);

        mainPanel.add(buttonPanel);

        this.add(critView);
        this.add(mainPanel);
        refresh();
    }

    public void refresh() {
        removeAllActionListeners();
        critView.refresh();
        unallocatedView.refresh();
        addAllActionListeners();
    }

    public JLabel createLabel(String text, Dimension maxSize) {

        JLabel label = new JLabel(text, SwingConstants.TRAILING);

        label.setMaximumSize(maxSize);
        label.setMinimumSize(maxSize);
        label.setPreferredSize(maxSize);

        return label;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand().equals(AUTOFILLCOMMAND)) {
            autoFillCrits();
        } else if (evt.getActionCommand().equals(RESETCOMMAND)) {
            resetCrits();
        }
    }

    private void autoFillCrits() {

        for (Mounted<?> mount : unallocatedView.getTableModel().getCrits()) {
            for (int location = 0; location < getTank().locations(); location++) {
                try {
                    getTank().addEquipment(mount, location, false);
                    UnitUtil.changeMountStatus(getTank(), mount, location, Entity.LOC_NONE, false);
                    break;
                } catch (Exception ex) {
                    logger.error("", ex);
                }
            }
        }
        refresh.refreshAll();

    }

    private void resetCrits() {
        UnitUtil.removeAllCriticals(getTank());
        // Check linking after you remove everything.
        try {
            MekFileParser.postLoadInit(getTank());
        } catch (EntityLoadingException ele) {
            // do nothing.
        } catch (Exception ex) {
            logger.error("", ex);
        }

        refresh.refreshAll();
    }

    private void removeAllActionListeners() {
        autoFillButton.removeActionListener(this);
        resetButton.removeActionListener(this);
    }

    private void addAllActionListeners() {
        autoFillButton.addActionListener(this);
        resetButton.addActionListener(this);
    }

    public void addRefreshedListener(RefreshListener l) {
        refresh = l;
        critView.updateRefresh(refresh);
        unallocatedView.addRefreshedListener(refresh);
    }
}
