/*
 * MegaMekLab - Copyright (C) 2020 - The MegaMek Team
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
package megameklab.printing.reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import megamek.common.AmmoType;
import megamek.common.BattleArmor;
import megamek.common.Compute;
import megamek.common.Entity;
import megamek.common.Infantry;
import megamek.common.MiscType;
import megamek.common.Mounted;
import megamek.common.WeaponType;
import megamek.common.weapons.missiles.MissileWeapon;
import megameklab.printing.PrintEntity;
import megameklab.printing.PrintRecordSheet;

/**
 * Table showing the relevant columns of the cluster hits table
 */
public class ClusterHitsTable extends ReferenceTable {
    protected final Set<Integer> clusterSizes = new TreeSet<>();
    protected boolean hasATM;
    protected boolean hasHAG;
    protected boolean condensed;

    public ClusterHitsTable(PrintEntity sheet, boolean condensed) {
        this(sheet, sheet.getEntity(), condensed);
    }

    public ClusterHitsTable(PrintRecordSheet sheet, Entity entity, boolean condensed) {
        super(sheet);
        this.condensed = condensed;
        calculateClusterSizes(entity);
        addTable(entity);
    }

    public ClusterHitsTable(PrintRecordSheet sheet, List<Entity> entities, boolean condensed) {
        super(sheet);
        this.condensed = condensed;
        for (Entity en : entities) {
            calculateClusterSizes(en);
        }
        addTable(entities.get(0));
    }

    public Set<Integer> getSizes() {
        return clusterSizes;
    }

    protected void addTable(Entity entity) {
        if (!clusterSizes.isEmpty()) {
            List<Double> offsets = new ArrayList<>();
            double spacing = 0.9 / (clusterSizes.size() + 1);
            for (double o = 0.05 + spacing / 2.0; o <= 0.95; o += spacing) {
                offsets.add(o);
            }
            setColOffsets(offsets);
            List<String> headers = new ArrayList<>();
            if (!condensed) {
                headers.add(bundle.getString("dieRoll2d6"));
            } else {
                headers.add(bundle.getString("2d6"));
            }
            for (int size : clusterSizes) {
                headers.add(String.valueOf(size));
            }
            setHeaders(headers);
            addRows();
            addNotes(entity);
        }
    }

    private void calculateClusterSizes(Entity entity) {
        if (entity instanceof Infantry) {
            int size = ((Infantry) entity).getShootingStrength();
            for (int i = 2; i <= size; i++) {
                clusterSizes.add(i);
            }
            if (entity instanceof BattleArmor) {
                for (Mounted<?> mounted : entity.getIndividualWeaponList()) {
                    // We skip equipment that has critical slots and is not in a valid mount location
                    if ((mounted.getCriticals()>0) && (mounted.getBaMountLoc() == BattleArmor.MOUNT_LOC_NONE)) continue;
                    if (mounted.getType() instanceof MissileWeapon) {
                        for (int troopers = 1; troopers <= size; troopers++) {
                            clusterSizes.add(Math.min(40, troopers * ((MissileWeapon) mounted.getType()).getRackSize()));
                        }
                    }
                }
            }
            return;
        }
        for (Mounted<?> mounted : entity.getIndividualWeaponList()) {
            if (mounted.getType() instanceof WeaponType) {
                final WeaponType weapon = (WeaponType) mounted.getType();
                switch (weapon.getAmmoType()) {
                    case AC_LBX:
                    case EXLRM:
                    case IATM:
                    case LRM:
                    case LRM_IMP:
                    case LRM_PRIMITIVE:
                    case LRM_TORPEDO:
                    case LRM_STREAK:
                    case MML:
                    case MRM:
                    case NLRM:
                    case ROCKET_LAUNCHER:
                    case SBGAUSS:
                    case SRM:
                    case SRM_ADVANCED:
                    case SRM_IMP:
                    case SRM_PRIMITIVE:
                    case SRM_TORPEDO:
                    case SRM_STREAK:
                        clusterSizes.add(weapon.getRackSize());
                        break;
                    case ATM:
                        hasATM = true;
                        clusterSizes.add(weapon.getRackSize());
                        break;
                    case HAG:
                        hasHAG = true;
                        clusterSizes.add(weapon.getRackSize());
                        break;
                    case AC_ROTARY:
                        for (int i = 2; i <= 6; i++) {
                            clusterSizes.add(i);
                        }
                        break;
                    case AC_ULTRA:
                    case AC_ULTRA_THB:
                        clusterSizes.add(2);
                        break;
                    case MG:
                    case MG_HEAVY:
                    case MG_LIGHT:
                        if (weapon.hasFlag(WeaponType.F_MGA)) {
                            for (int i = 2; i <= 4; i++) {
                                clusterSizes.add(i);
                            }
                        }
                        break;
                }
            }
        }
    }

    protected void addRows() {
        for (int roll = 2; roll <= 12; roll++) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(roll));
            for (int size : clusterSizes) {
                row.add(String.valueOf(Compute.calculateClusterHitTableAmount(roll, size)));
            }
            addRow(row);
        }
    }

    protected void addNotes(Entity entity) {
        if (hasATM || entity.hasWorkingMisc(MiscType.F_ARTEMIS)) {
            addNote(bundle.getString("artemisIV.note"));
        } else if (entity.hasWorkingMisc(MiscType.F_ARTEMIS_V)) {
            addNote(bundle.getString("artemisV.note"));
        } else if (entity.hasWorkingMisc(MiscType.F_ARTEMIS_PROTO)) {
            addNote(bundle.getString("artemisProto.note"));
        }
        if (entity.hasWorkingMisc(MiscType.F_APOLLO)) {
            addNote(bundle.getString("apollo.note"));
        }
        if (hasHAG) {
            addNote(bundle.getString("hag.note"));
        }
    }

    /**
     * @return Whether the unit has any weapons that use the cluster hits table
     */
    public boolean required() {
        return !clusterSizes.isEmpty();
    }

    public int columnCount() {
        return clusterSizes.size();
    }
}
