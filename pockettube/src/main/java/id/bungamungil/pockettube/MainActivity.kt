package id.bungamungil.pockettube

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.yausername.youtubedl_android.mapper.VideoInfo
import id.bungamungil.pockettube.databinding.ActivityMainBinding
import id.bungamungil.pockettube.service.DownloadCallable
import id.bungamungil.pockettube.service.DownloadService

class MainActivity : AppCompatActivity(), MainView {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener {
            val serviceIntent = Intent(this, DownloadService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun showDownloadButton(videoInfo: VideoInfo) {
        binding.fab.setOnClickListener {
            val intent = Intent(this, DownloadService::class.java)
            intent.putExtra(DownloadCallable.DOWNLOAD_URL, videoInfo.webpageUrl)
            intent.putExtra(DownloadCallable.DOWNLOAD_FORMAT, videoInfo.formatId)
            intent.putExtra(DownloadCallable.DOWNLOAD_NAME, "${videoInfo.extractor}-${videoInfo.id}")
            intent.putExtra(DownloadCallable.DOWNLOAD_ID, DownloadService.counter)
            intent.putExtra(DownloadCallable.DOWNLOAD_DISPLAY_NAME, videoInfo.title)
            ContextCompat.startForegroundService(this, intent)
        }
        binding.fab.show()
    }

    override fun hideDownloadButton() {
        binding.fab.setOnClickListener(null)
        binding.fab.hide()
    }

}