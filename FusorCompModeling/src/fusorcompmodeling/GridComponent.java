/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusorcompmodeling;

import java.util.Random;

/**
 *
 * @author guberti
 */
public abstract class GridComponent {
    Vector pos; // Location
    double radius;
    int charge;
    
    public abstract Point getRandomPoint(Random r);
    
    public abstract double getSurfaceArea();
}
