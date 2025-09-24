<?php

namespace App\Exports;

use App\Models\Mesa;
use Maatwebsite\Excel\Concerns\FromCollection;

class MesasExport implements FromCollection
{
    /**
    * @return \Illuminate\Support\Collection
    */
    public function collection()
    {
        return Mesa::all();
    }
}
