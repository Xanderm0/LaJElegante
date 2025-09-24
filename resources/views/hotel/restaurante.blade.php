@extends('hotel.layouts.header')

@section('title', 'Restaurante - Hotel LJE')

@section('content')
<div class="container-fluid my-4">
    <div class="row" style="min-height: 80vh;">
        
        {{-- Columna izquierda: Restaurante --}}
        <div class="col-md-6 d-flex flex-column justify-content-between text-center bg-light bg-restaurant p-4">
            
            {{-- Texto intuitivo arriba --}}
            <div class="mb-auto">
                <div class="d-flex flex-column align-items-center">
                    <h4 class="fw-bold text-white">Mesas disponibles para hoy</h4>
                    <h5 class="text-white">Consulta en tiempo real la disponibilidad</h5>
                    <h6 class="text-white">--#--</h6>
                </div>
            </div>

            {{-- Botón abajo --}}
            <div class="mt-auto pb-3">
                <a href="#" class="btn btn-reserva btn-lg px-5">¡Reservar ahora!</a>
            </div>
        </div>

        {{-- Columna derecha: Platos --}}
        <div class="col-md-6 d-flex flex-column align-items-center justify-content-center">
            <div class="row g-3 h-100">
                
                {{-- Plato 1 --}}
                <div class="col-md-6">
                    <div class="d-flex flex-column h-100 overflow-hidden bg-prom">
                        <div class="flex-fill plato-img" style="background-image: url('{{ asset('build/img/plato1.png') }}');"></div>
                        <div class="flex-fill d-flex flex-column justify-content-center align-items-center p-3 text-white">
                            <h3 class="fw-bold">Ajiaco Santafereño</h3>
                            <div class="circle-card mt-2 mx-auto">$23.000</div>
                        </div>
                    </div>
                </div>

                {{-- Plato 2 --}}
                <div class="col-md-6">
                    <div class="d-flex flex-column h-100 overflow-hidden bg-prom">
                        <div class="flex-fill plato-img" style="background-image: url('{{ asset('build/img/plato2.png') }}');"></div>

                        <div class="flex-fill d-flex flex-column justify-content-center align-items-center p-3 text-white">
                            <h3 class="fw-bold">Corrientazo del día</h3>
                            <div class="circle-card mt-2 mx-auto">$20.500</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection