package im.hengxin.permissionlab

import android.os.Bundle
import com.getcapacitor.BridgeActivity

class MainActivity : BridgeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        registerPlugin(AndroidPermissionBrokerPlugin::class.java)
        super.onCreate(savedInstanceState)
    }
}
