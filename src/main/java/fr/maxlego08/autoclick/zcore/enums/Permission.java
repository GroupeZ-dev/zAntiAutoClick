package fr.maxlego08.autoclick.zcore.enums;

public enum Permission {

    ZANTIAUTOCLICK_USE,
    ZANTIAUTOCLICK_RELOAD,
    ZANTIAUTOCLICK_SHOW,
    ZANTIAUTOCLICK_SHOW_ALL,
    ZANTIAUTOCLICK_SUSPECT,

    ZANTIAUTOCLICK_CLEAN;

    private final String permission;

    Permission() {
        this.permission = this.name().toLowerCase().replace("_", ".");
    }

    public String getPermission() {
        return permission;
    }

}
