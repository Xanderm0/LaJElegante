@extends('hotel.layouts.header')

@section('title', 'Login Empleados - Hotel LJE')

@section('content')
<div class="container d-flex justify-content-center align-items-center" style="min-height: 80vh;">
    <div class="card shadow-lg p-4" style="max-width: 400px; width: 100%;">
        <h3 class="text-center mb-4 fw-bold">🔑 Login Empleados</h3>

        @if ($errors->any())
            <div class="alert alert-danger">
                <ul class="mb-0">
                    @foreach ($errors->all() as $error)
                        <li>{{ $error }}</li>
                    @endforeach
                </ul>
            </div>
        @endif

        <form method="POST" action="{{ route('hotel.empleados.login.process') }}">
            @csrf
            <div class="mb-3">
                <label for="email" class="form-label">Correo electrónico</label>
                <input type="email" name="email" id="email" 
                       class="form-control" placeholder="ejemplo@email.com" required autofocus>
            </div>

            <div class="mb-3">
                <label for="password" class="form-label">Contraseña</label>
                <input type="password" name="password" id="password" 
                       class="form-control" placeholder="••••••••" required>
            </div>

            <button type="submit" class="btn btn-reserva w-100">Ingresar</button>
        </form>

        <hr class="my-4">

        <p class="text-center mb-0">
            ¿Eres huésped? 
            <a href="{{ route('hotel.login.index') }}" class="text-decoration-none fw-bold text-primary">
                ingresa aquí
            </a>
        </p>
    </div>
</div>
@endsection