package com.example.adminuplaod;

public class Count {

    private int celeb;
    private int cars;
    private int building;
    private int nature;
    private int space;
    private int ocean;

    public Count(int celeb, int cars, int building, int nature, int space, int ocean) {
        this.celeb = celeb;
        this.cars = cars;
        this.building = building;
        this.nature = nature;
        this.space = space;
        this.ocean = ocean;
    }

    public Count() {
    }

    public int getCeleb() {
        return celeb;
    }

    public void setCeleb(int celeb) {
        this.celeb = celeb;
    }

    public int getCars() {
        return cars;
    }

    public void setCars(int cars) {
        this.cars = cars;
    }

    public int getBuilding() {
        return building;
    }

    public void setBuilding(int building) {
        this.building = building;
    }

    public int getNature() {
        return nature;
    }

    public void setNature(int nature) {
        this.nature = nature;
    }

    public int getSpace() {
        return space;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public int getOcean() {
        return ocean;
    }

    public void setOcean(int ocean) {
        this.ocean = ocean;
    }
}
