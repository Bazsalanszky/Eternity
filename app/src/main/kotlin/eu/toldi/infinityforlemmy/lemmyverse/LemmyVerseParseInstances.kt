package eu.toldi.infinityforlemmy.lemmyverse

import org.json.JSONArray

object LemmyVerseParseInstances {
    fun parseInstances(body: String?): List<LemmyInstance> {
        val instances: MutableList<LemmyInstance> = ArrayList()
        try {
            val jsonBody = JSONArray(body)
            for (i in 0 until jsonBody.length()) {
                val jsonInstance = jsonBody.getJSONObject(i)

                val name = jsonInstance.getString("name")
                val url = jsonInstance.getString("base")
                instances.add(LemmyInstance(name, url))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return instances
    }

}
