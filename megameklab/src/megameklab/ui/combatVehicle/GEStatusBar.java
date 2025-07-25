/*
 * Copyright (C) 2025 The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MegaMekLab.
 *
 * MegaMekLab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPL),
 * version 3 or (at your option) any later version,
 * as published by the Free Software Foundation.
 *
 * MegaMekLab is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * A copy of the GPL should have been included with this project;
 * if not, see <https://www.gnu.org/licenses/>.
 *
 * NOTICE: The MegaMek organization is a non-profit group of volunteers
 * creating free software for the BattleTech community.
 *
 * MechWarrior, BattleMech, `Mech and AeroTech are registered trademarks
 * of The Topps Company, Inc. All Rights Reserved.
 *
 * Catalyst Game Labs and the Catalyst Game Labs logo are trademarks of
 * InMediaRes Productions, LLC.
 *
 * MechWarrior Copyright Microsoft Corporation. MegaMekLab was created under
 * Microsoft's "Game Content Usage Rules"
 * <https://www.xbox.com/en-US/developers/rules> and it is not endorsed by or
 * affiliated with Microsoft.
 */
package megameklab.ui.combatVehicle;

import megamek.client.ui.clientGUI.calculationReport.CalculationReport;
import megamek.common.Mounted;
import megameklab.ui.generalUnit.StatusBar;

class GEStatusBar extends StatusBar {

    private static final String WEIGHT_LABEL = "Weight: %s t";

    GEStatusBar(GEMainUI parent) {
        super(parent);
    }

    @Override
    protected void refreshWeight() {
        double tonnage = getEntity().getEquipment().stream().mapToDouble(Mounted::getTonnage).sum();
        String full = CalculationReport.formatForReport(tonnage);
        tons.setText(String.format(WEIGHT_LABEL, full));
        tons.setToolTipText("Click to show the weight calculation.");
    }
}
