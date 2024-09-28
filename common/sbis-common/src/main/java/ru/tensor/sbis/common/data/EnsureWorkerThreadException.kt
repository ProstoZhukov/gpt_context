package ru.tensor.sbis.common.data

class EnsureWorkerThreadException: RuntimeException("Dependency must be called on the worker thread")