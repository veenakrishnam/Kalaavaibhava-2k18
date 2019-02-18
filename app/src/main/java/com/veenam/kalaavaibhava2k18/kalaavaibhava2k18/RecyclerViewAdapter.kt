package com.veenam.kalaavaibhava2k18.kalaavaibhava2k18

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.lang.ref.WeakReference


val BASE_URL = "**your url comes here**"

class EventRecyclerViewAdapter(context: Context): RecyclerView.Adapter<EventRecyclerViewAdapter.EventViewHolder>(){
    val items = ArrayList<EventsData>()
    var myListener: EventItemClickListener? = null
    var lastPosition = -1

    private lateinit var mMemoryCache: LruCache<String, Bitmap>

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8

        mMemoryCache = object: LruCache<String, Bitmap>(cacheSize){
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.byteCount / 1024
            }
        }

        val url = BASE_URL + "events.php"
        val task = DownloadEvents(items)
        task.execute(url)
    }

    interface EventItemClickListener {
        fun onItemClickFromAdapter(eventsData: EventsData)
    }

    inner class EventViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val eventCard = view.findViewById<CardView>(R.id.eventCard)
        val eventTitle = view.findViewById<TextView>(R.id.eventTitle)
        val eventImage = view.findViewById<ImageView>(R.id.eventImage)
        val event_date_time = view.findViewById<TextView>(R.id.event_date_time)
        val eventMoreInfoButton = view.findViewById<Button>(R.id.eventMoreInfoButton)

        init {
            eventMoreInfoButton.setOnClickListener {
                if (myListener != null) {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        myListener!!.onItemClickFromAdapter(items[adapterPosition])
                    }
                }
            }
        }
    }

    inner class DownloadEvents(data: ArrayList<EventsData>): AsyncTask<String, Void, String>(){
        val weakData = WeakReference<ArrayList<EventsData>>(data)
        override fun doInBackground(vararg params: String?): String {
            var result :String? = " "
            result = MyUtility.downloadJSONusingHTTPGetRequest(params[0]!!)
            //Log.i("Events ",result)
            return result!!
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            var list = weakData.get()
            if (list != null){
                list.clear()

                if (result!= " "){
                    val events = Gson().fromJson(result, Array<EventsData>::class.java).asList()
                    for (event in events){
                        list.add(event)
                    }
                }
                else{
                    val errorresult = "[{\"id\":1,\"name\":\"Please Connect to the internet\",\"imageUrl\":\" \"}]"
                    val events = Gson().fromJson(errorresult, Array<EventsData>::class.java).asList()
                    for (event in events){
                        list.add(event)
                    }
                }
                notifyDataSetChanged()
            }
        }

    }

    inner class DownloadEventImage(img :ImageView): AsyncTask<String, Void, Bitmap>() {
        val weakImg = WeakReference<ImageView>(img)
        override fun doInBackground(vararg params: String?): Bitmap {
            var errorresult: Bitmap? = null
            errorresult = Bitmap.createBitmap(50,50,Bitmap.Config.ARGB_8888)

            val result = MyUtility.downloadImageusingHTTPGetRequest(params[0]!!)
            if(result != null){
                mMemoryCache.put(params[0]!!, result)
            }else{
                return errorresult
            }
            return result!!
        }

        // after completing download -> this is changing main UI thread 's View
        override fun onPostExecute(result :Bitmap?) {
            super.onPostExecute(result)
            val img = weakImg.get()
            if(img != null){
                img.setImageBitmap(result)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val view: View

        view = when (viewType) {
            0, 1, 2 -> {
                layoutInflater.inflate(R.layout.rv_events, parent, false)
            }
            (items.size - 1), (items.size - 2), (items.size - 3) -> {
                layoutInflater.inflate(R.layout.rv_events, parent, false)
            }
            else -> {
                layoutInflater.inflate(R.layout.rv_events, parent, false)
            }
        }

        return EventViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = items[position]

        holder.eventTitle.text = event.title
        holder.event_date_time.text = event.date + " - " + event.time

        setAnimationLeft(holder.eventCard, position)

        if (event.title == "Please Connect to the internet"){
            holder.eventCard.visibility = GONE
        }

        val url = event.imageUrl!!
        val bitmap = mMemoryCache.get(url)
        if(bitmap != null){
            // if the url is found in the cache
            holder.eventImage.setImageBitmap(bitmap)
        }
        else{
            val task = DownloadEventImage(holder.eventImage)
            task.execute(url)
        }
    }

    fun setAnimationLeft(view: View, position: Int) {
        if (position != lastPosition) {
            val animation = AnimationUtils.loadAnimation(view.context, android.R.anim.slide_in_left)
            animation.duration = 1000L
            view.startAnimation(animation)

            lastPosition = position
        }
    }

    fun setItemClickListener(listener: EventItemClickListener) {
        this.myListener = listener
    }

}

class SponsorsRecyclerViewAdapter(context: Context): RecyclerView.Adapter<SponsorsRecyclerViewAdapter.SponsorViewHolder>(){
    val items = ArrayList<SponsorsData>()
    var lastPosition = -1

    private lateinit var mMemoryCache: LruCache<String, Bitmap>

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8

        mMemoryCache = object: LruCache<String, Bitmap>(cacheSize){
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.byteCount / 1024
            }
        }

        val url = BASE_URL + "sponsors.php"
        val task = DownloadSponsors(items)
        task.execute(url)
    }

    inner class SponsorViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val sponsorCard = view.findViewById<CardView>(R.id.sponsorCard)
        val sponsorName = view.findViewById<TextView>(R.id.sponsorName)
        val sponsorImage = view.findViewById<ImageView>(R.id.sponsorImage)
    }

    inner class DownloadSponsors(data: ArrayList<SponsorsData>): AsyncTask<String, Void, String>(){
        val weakData = WeakReference<ArrayList<SponsorsData>>(data)
        override fun doInBackground(vararg params: String?): String {
            var result :String? = " "
            result = MyUtility.downloadJSONusingHTTPGetRequest(params[0]!!)
            //Log.i("Sponsors ",result)
            return result!!
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            var list = weakData.get()
            if (list != null){
                list.clear()

                if (result!= " "){
                    val sponsors = Gson().fromJson(result, Array<SponsorsData>::class.java).asList()
                    for (sponsor in sponsors){
                        list.add(sponsor)
                    }
                }
                else{
                    val errorresult = "[{\"id\":1,\"name\":\"Please Connect to the internet\",\"imageUrl\":\" \"}]"
                    val sponsors = Gson().fromJson(errorresult, Array<SponsorsData>::class.java).asList()
                    for (sponsor in sponsors){
                        list.add(sponsor)
                    }
                }
                notifyDataSetChanged()
            }
        }

    }

    inner class DownloadSponsorImage(img :ImageView): AsyncTask<String, Void, Bitmap>() {
        val weakImg = WeakReference<ImageView>(img)
        override fun doInBackground(vararg params: String?): Bitmap {
            var errorresult: Bitmap? = null
            errorresult = Bitmap.createBitmap(50,50,Bitmap.Config.ARGB_8888)

            val result = MyUtility.downloadImageusingHTTPGetRequest(params[0]!!)
            if(result != null){
                mMemoryCache.put(params[0]!!, result)
            }else{
                return errorresult
            }
            return result!!
        }

        // after completing download -> this is changing main UI thread 's View
        override fun onPostExecute(result :Bitmap?) {
            super.onPostExecute(result)
            val img = weakImg.get()
            if(img != null){
                img.setImageBitmap(result)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SponsorViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val view: View

        view = when (viewType) {
            0, 1, 2 -> {
                layoutInflater.inflate(R.layout.rv_sponsors, parent, false)
            }
            (items.size - 1), (items.size - 2), (items.size - 3) -> {
                layoutInflater.inflate(R.layout.rv_sponsors, parent, false)
            }
            else -> {
                layoutInflater.inflate(R.layout.rv_sponsors, parent, false)
            }
        }

        return SponsorViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SponsorViewHolder, position: Int) {
        val sponsor = items[position]

        holder.sponsorName.text = sponsor.name

        setAnimationLeft(holder.sponsorCard, position)

        val url = sponsor.imageUrl!!
        val bitmap = mMemoryCache.get(url)
        if(bitmap != null){
            // if the url is found in the cache
            holder.sponsorImage.setImageBitmap(bitmap)
        }
        else{
            val task = DownloadSponsorImage(holder.sponsorImage)
            task.execute(url)
        }
    }

    fun setAnimationLeft(view: View, position: Int) {
        if (position != lastPosition) {
            val animation = AnimationUtils.loadAnimation(view.context, android.R.anim.slide_in_left)
            animation.duration = 1000L
            view.startAnimation(animation)

            lastPosition = position
        }
    }

}

class CrewRecyclerViewAdapter(context: Context): RecyclerView.Adapter<CrewRecyclerViewAdapter.CrewViewHolder>(){
    val items = ArrayList<CrewsData>()
    var lastPosition = -1

    private lateinit var mMemoryCache: LruCache<String, Bitmap>

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8

        mMemoryCache = object: LruCache<String, Bitmap>(cacheSize){
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.byteCount / 1024
            }
        }

        val url = BASE_URL + "crews.php"
        val task = DownloadCrews(items)
        task.execute(url)
    }

    inner class CrewViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val crewCard = view.findViewById<CardView>(R.id.crewCard)
        val crewCard2 = view.findViewById<CardView>(R.id.crew_card2)
        val crewName = view.findViewById<TextView>(R.id.crewName)
        val crewImage = view.findViewById<ImageView>(R.id.crewImage)
        val crew_semester_department = view.findViewById<TextView>(R.id.crew_semester_department)
        val crew_contact_number = view.findViewById<TextView>(R.id.crew_contact_number)
        val crew_email = view.findViewById<TextView>(R.id.crew_email)
    }

    inner class DownloadCrews(data: ArrayList<CrewsData>): AsyncTask<String, Void, String>(){
        val weakData = WeakReference<ArrayList<CrewsData>>(data)
        override fun doInBackground(vararg params: String?): String {
            var result :String? = " "
            result = MyUtility.downloadJSONusingHTTPGetRequest(params[0]!!)
            //Log.i("Crews ",result)
            return result!!
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            var list = weakData.get()
            if (list != null){
                list.clear()

                if (result!= " "){
                    val crews = Gson().fromJson(result, Array<CrewsData>::class.java).asList()
                    for (crew in crews){
                        list.add(crew)
                    }
                }
                else{
                    val errorresult = "[{\"id\":1,\"name\":\"Please Connect to the internet\",\"imageUrl\":\" \"}]"
                    val crews = Gson().fromJson(errorresult, Array<CrewsData>::class.java).asList()
                    for (crew in crews){
                        list.add(crew)
                    }
                }
                notifyDataSetChanged()
            }
        }

    }

    inner class DownloadCrewImage(img :ImageView): AsyncTask<String, Void, Bitmap>() {
        val weakImg = WeakReference<ImageView>(img)
        override fun doInBackground(vararg params: String?): Bitmap {
            var errorresult: Bitmap? = null
            errorresult = Bitmap.createBitmap(50,50,Bitmap.Config.ARGB_8888)

            val result = MyUtility.downloadImageusingHTTPGetRequest(params[0]!!)
            if(result != null){
                mMemoryCache.put(params[0]!!, result)
            }else{
                return errorresult
            }
            return result!!
        }

        // after completing download -> this is changing main UI thread 's View
        override fun onPostExecute(result :Bitmap?) {
            super.onPostExecute(result)
            val img = weakImg.get()
            if(img != null){
                img.setImageBitmap(result)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrewViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val view: View

        view = when (viewType) {
            0, 1, 2 -> {
                layoutInflater.inflate(R.layout.rv_crew, parent, false)
            }
            (items.size - 1), (items.size - 2), (items.size - 3) -> {
                layoutInflater.inflate(R.layout.rv_crew, parent, false)
            }
            else -> {
                layoutInflater.inflate(R.layout.rv_crew, parent, false)
            }
        }

        return CrewViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CrewViewHolder, position: Int) {
        val crew = items[position]

        holder.crewName.text = crew.name
        holder.crew_semester_department.text = crew.semester_department
        holder.crew_contact_number.text = crew.contactNumber
        holder.crew_email.text = crew.email

        setAnimationLeft(holder.crewCard, position)

        if (crew.name == "Please Connect to the internet"){
            holder.crewCard2.visibility = GONE
        }

        val url = crew.imageUrl!!
        val bitmap = mMemoryCache.get(url)
        if(bitmap != null){
            // if the url is found in the cache
            holder.crewImage.setImageBitmap(bitmap)
        }
        else{
            val task = DownloadCrewImage(holder.crewImage)
            task.execute(url)
        }
    }

    fun setAnimationLeft(view: View, position: Int) {
        if (position != lastPosition) {
            val animation = AnimationUtils.loadAnimation(view.context, android.R.anim.slide_in_left)
            animation.duration = 1000L
            view.startAnimation(animation)

            lastPosition = position
        }
    }

}