package com.orbotix.sample.locator;

import orbotix.sphero.Sphero;

/**
 * Created: 13/12/2013
 *
 * @author ryanjohn
 */
public class KotikanColors {

    int[] redArray = new int[]{220, 128, 0, 105, 234, 224};
    int[] greenArray = new int[]{4, 55, 169, 190, 171, 82};
    int[] blueArray = new int[]{81, 155, 224, 40, 0, 6};
    int currentColor = 0;

    public void KotikanColors() {
    }

    public void setNextColor(Sphero robot) {
        robot.setColor(redArray[currentColor],greenArray[currentColor], blueArray[currentColor]);
        currentColor++;
        if(currentColor == 6) currentColor = 0;
    }
}
