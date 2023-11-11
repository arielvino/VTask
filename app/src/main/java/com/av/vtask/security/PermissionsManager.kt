package com.av.vtask.security

object PermissionsManager {
    private var addPermit = true
    private var deletePermit = true
    private var changePermit = true
    private var readPermit = true
    private var securityPermit = false

    fun allowedTo(action: Permissions): Boolean {
        when (action) {
            Permissions.Add -> {
                return addPermit
            }

            Permissions.Delete -> {
                return deletePermit
            }

            Permissions.Read -> {
                return changePermit
            }

            Permissions.Change -> {
                return readPermit
            }

            Permissions.Security ->{
                return securityPermit
            }
        }
    }
}