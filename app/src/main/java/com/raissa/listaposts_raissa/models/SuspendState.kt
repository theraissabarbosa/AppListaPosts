package com.raissa.listaposts_raissa.models

// SuccessDataType tipo GENERICO - voce define depois
// OUT = só produz do tipo (IN seria somente consumo | se não colocar nada = aceita os dois)
sealed class SuspendState<out SuccessDataType>

class SuccessState<out SuccessDataType>(val data: SuccessDataType): SuspendState<SuccessDataType>()
class ErrorState(val message : String): SuspendState<Nothing>()
class LoadingState: SuspendState<Nothing>()
