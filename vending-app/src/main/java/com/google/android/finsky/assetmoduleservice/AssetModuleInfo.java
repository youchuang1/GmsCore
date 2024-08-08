package com.google.android.finsky.assetmoduleservice;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;

class AssetModuleInfo {
    private PackageManager packageManager;
    private Object assetModule;
    private Object authHandler;
    private Object ownershipChecker;

    public AssetModuleInfo(Context context, PackageManager packageManager, Object assetModule, Object authHandler, Object ownershipChecker) {
        this.packageManager = packageManager;
        this.assetModule = assetModule;
        this.authHandler = authHandler;
        this.ownershipChecker = ownershipChecker;
    }

    public final int checkPackagePermissions(String packageName, int someValue) {
        if (packageName != null) {
            int callingUid = Binder.getCallingUid();
            String[] callerPackages = packageManager.getPackagesForUid(callingUid);
            if (callerPackages != null && containsPackageName(callerPackages, packageName)) {
                if (!isPermittedByGlobalFlag(packageName, ((AssetModuleController) this.assetModule).getConfig("AssetModules", "asset_module_package_controller"))) {
                    return -5;
                }
                if (!((AuthenticationHandler) this.authHandler).isAuthenticated()) {
                    return -5;
                }
                if (!((OwnershipChecker) this.ownershipChecker).isOwned(packageName) && !hasSpecialPermission()) {
                    return isSpecialConditionMet(((AssetModuleController) this.assetModule), someValue) ? -13 : -5;
                }
                return 0;
            }
        }
        return -5;
    }


    private static boolean containsPackageName(String[] packageNames, String targetPackage) {
        for (String packageName : packageNames) {
            if (packageName.equals(targetPackage)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPermittedByGlobalFlag(String packageName, String globalFlag) {
        return true;
    }

    private static boolean hasSpecialPermission() {
        return true;
    }

    private static boolean isSpecialConditionMet(AssetModuleController assetModuleController, int value) {
        return true;
    }
}


class AssetModuleController {
    public String getConfig(String module, String controller) {
        return "someConfig";
    }
}

class AuthenticationHandler {
    public boolean isAuthenticated() {
        return true;
    }
}

class OwnershipChecker {
    public boolean isOwned(String packageName) {
        return true;
    }
}
