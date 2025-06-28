import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.diagnostic333.DigitalNumberWindowView
import com.example.diagnostic333.R
import com.example.diagnostic333.SeekBarView
import com.example.diagnostic333.SpeedometerView

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val speedometer1: SpeedometerView = view.findViewById(R.id.speedometer1)
        speedometer1.configure(0f, 220f, Color.WHITE)


        val speedometer2: SpeedometerView =  view.findViewById(R.id.speedometer2)
        speedometer2.configure(0f, 220f, Color.WHITE)


        val oilTempSeekBar: SeekBarView =  view.findViewById(R.id.oilTempSeekBar)
        oilTempSeekBar.configure(50f, 150f, 0f)


        val tirePressureSeekBar: SeekBarView =  view.findViewById(R.id.tirePressureSeekBar)
        tirePressureSeekBar.configure(20f, 40f,0f)


        val massAirFlowSeekBar: SeekBarView =  view.findViewById(R.id.massAirFlowSeekBar)
        massAirFlowSeekBar.configure(0f, 200f, 0f)


        val numberWindow =  view.findViewById<DigitalNumberWindowView>(R.id.numberWindow)
        numberWindow.configure(0f, 220f, Color.RED)
        numberWindow.updateValue(0f)


//        val handler = Handler(Looper.getMainLooper())
//        val runnable = object : Runnable {
//            override fun run() {
//                val randomValue1 = 20f + (0..200).random().toFloat()
//                speedometer1.updateValue(randomValue1)
//                numberWindow.updateValue(randomValue1)
//                val randomValue2 = 20f + (0..200).random().toFloat()
//                speedometer2.updateValue(randomValue2)
//                val oilTemp = 50f + (0..100).random().toFloat()
//                oilTempSeekBar.updateValue(oilTemp)
//                val tirePressure = 20f + (0..20).random().toFloat()
//                tirePressureSeekBar.updateValue(tirePressure)
//                val massAirFlow = 0f + (0..200).random().toFloat()
//                massAirFlowSeekBar.updateValue(massAirFlow)
//                handler.postDelayed(this, 2000)
//            }
//        }
//        handler.postDelayed(runnable, 2000)
    }
}