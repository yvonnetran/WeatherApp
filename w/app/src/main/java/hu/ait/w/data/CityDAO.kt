package hu.ait.w.data

import android.arch.persistence.room.*


@Dao
interface CityDAO {
    @Query("SELECT * FROM city")
    fun getAllCities(): List<City>

    @Insert
    fun insertCity(city: City): Long

    @Insert
    fun insertCities(vararg city: City): List<Long>

    @Delete
    fun deleteCity(city: City)

    @Update
    fun updateCity(city: City)
}