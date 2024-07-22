package com.google.android.finsky.assetmoduleservice;

import android.content.pm.PackageManager;
import android.os.Binder;

class AssetModuleInfo {
    private PackageManager packageManager;
    private Object assetModule;
    private Object authHandler;
    private Object ownershipChecker;

    public AssetModuleInfo(PackageManager packageManager, Object assetModule, Object authHandler, Object ownershipChecker) {
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
                    logError("Package name %s is not permitted by global flag.", packageName);
                    return -5;
                }

                if (!((AuthenticationHandler) this.authHandler).isAuthenticated()) {
                    logError("Unauthenticated asset module requests are not allowed");
                    return -5;
                }

                if (!((OwnershipChecker) this.ownershipChecker).isOwned(packageName) && !hasSpecialPermission()) {
                    logError("The app is not owned, package: %s", packageName);
                    return isSpecialConditionMet(((AssetModuleController) this.assetModule), someValue) ? -13 : -5;
                }

                return 0;
            }
        }

        logError("Package name %s is not owned by caller.", packageName);
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

    private static void logError(String message, Object... args) {
        System.err.printf(message + "\n", args);
    }
}

class PackageManagerHelper {
    public PackageManager d;
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
