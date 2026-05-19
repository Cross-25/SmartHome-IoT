package com.glassinc.smart_home.desktop;

public class Device {
    private Long id;
    private String name;
    private String type;
    private boolean state; // truue allumé, false -éteint

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public boolean isState() { return state; }

    // Setter pour pouvoir modifier l'état
    public void setState(boolean state) { this.state = state; }
}
