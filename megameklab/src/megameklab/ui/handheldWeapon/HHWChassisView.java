/*
 * MegaMekLab - Copyright (C) 2025 The MegaMek Team
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */

package megameklab.ui.handheldWeapon;

import megamek.common.Entity;
import megamek.common.HandheldWeapon;
import megameklab.ui.generalUnit.BuildView;
import megameklab.ui.listeners.HHWBuildListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;

public class HHWChassisView extends BuildView implements ChangeListener {
    List<HHWBuildListener> listeners = new CopyOnWriteArrayList<>();
    public void addListener(HHWBuildListener l) {
        listeners.add(l);
    }
    public void removeListener(HHWBuildListener l) {
        listeners.remove(l);
    }

    private final SpinnerNumberModel tonnageModel = new SpinnerNumberModel(1, 0.5, 100, 0.5);
    private final JSpinner spnTonnage = new JSpinner(tonnageModel);

    private final SpinnerNumberModel armorModel = new SpinnerNumberModel(0, 0, 1600, 1);
    private final JSpinner spnArmor = new JSpinner(armorModel);
    private final SpinnerNumberModel armorWeightModel = new SpinnerNumberModel(0, 0, 100, 0.5);
    private final JSpinner spnArmorWeight = new JSpinner(armorWeightModel);

    public HHWChassisView() {
        ResourceBundle resourceMap = ResourceBundle.getBundle("megameklab.resources.Views");
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        var lblWeight = new JLabel(resourceMap.getString("HHWChassisView.lblWeight.text"));
        lblWeight.setToolTipText(resourceMap.getString("HHWChassisView.lblWeight.tooltip"));
        add(lblWeight, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(spnTonnage, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        var lblArmor = new JLabel(resourceMap.getString("HHWChassisView.lblArmor.text"));
        lblArmor.setToolTipText(resourceMap.getString("HHWChassisView.lblArmor.tooltip"));
        add(lblArmor, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(spnArmor, gbc);
        gbc.gridx = 2;
        gbc.gridy = 1;
        add(spnArmorWeight, gbc);

        spnTonnage.addChangeListener(this);
        spnArmor.addChangeListener(this);
        spnArmorWeight.addChangeListener(this);
    }

    public void setFromEntity(Entity hhw) {
        spnTonnage.setValue(hhw.getWeight());
        spnArmor.setValue(hhw.getOArmor(HandheldWeapon.LOC_GUN));
        spnArmorWeight.setValue(hhw.getArmorWeight());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == spnTonnage) {
            listeners.forEach(l -> l.weightChanged(tonnageModel.getNumber().doubleValue()));
        } else if (e.getSource() == spnArmor) {
            listeners.forEach(l -> l.armorChanged(armorModel.getNumber().intValue()));
        } else if (e.getSource() == spnArmorWeight) {
            listeners.forEach(l -> l.armorChanged((int) (armorWeightModel.getNumber().doubleValue() * 16)));
        }
    }
}
