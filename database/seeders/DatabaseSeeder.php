<?php

namespace Database\Seeders;

use App\Models\ReservaHabitacion;
use App\Models\ReservaRestaurante;
use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        $this->call([
            TipoClienteSeeder::class,
            TipoHabitacionSeeder::class,
            HabitacionSeeder::class,
            TemporadaSeeder::class,
            TarifaSeeder::class,
            MesaSeeder::class,
            UserSeeder::class,
            ClienteSeeder::class,
            ReservaHabitacion::class,
            ReservaRestaurante::class
        ]);
    }
}