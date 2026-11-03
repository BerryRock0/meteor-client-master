package com.viaversion.viaversion.libs.mcstructs.converter.codec.map;

import com.viaversion.viaversion.libs.mcstructs.converter.DataConverter;
import com.viaversion.viaversion.libs.mcstructs.converter.codec.Codec;
import com.viaversion.viaversion.libs.mcstructs.converter.mapcodec.MapCodec;
import com.viaversion.viaversion.libs.mcstructs.converter.model.Result;

import java.util.Map;
import java.util.function.Function;

public class MapCodecMerger {

    public static <T1, O> MapCodec<O> mapCodec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final I1<T1, O> constructor
    ) {
        return new MapCodec<O>() {
            @Override
            public <S> Result<Map<S, S>> serialize(DataConverter<S> converter, Map<S, S> map, O element) {
                Result<Map<S, S>> result = codec0.serialize(converter, map, getter0.apply(element));
                if (result.isError()) return result.mapError();
                return result;
            }

            @Override
            public <S> Result<O> deserialize(DataConverter<S> converter, Map<S, S> map) {
                Result<T1> result0 = codec0.deserialize(converter, map);
                if (result0.isError()) return result0.mapError();
                return Result.success(constructor.apply(result0.get()));
            }
        };
    }

    public static <T1, O> Codec<O> codec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final I1<T1, O> constructor
    ) {
        return mapCodec(codec0, getter0, constructor).asCodec();
    }

    public static <T1, T2, O> MapCodec<O> mapCodec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final I2<T1, T2, O> constructor
    ) {
        return new MapCodec<O>() {
            @Override
            public <S> Result<Map<S, S>> serialize(DataConverter<S> converter, Map<S, S> map, O element) {
                Result<Map<S, S>> result = codec0.serialize(converter, map, getter0.apply(element));
                if (result.isError()) return result.mapError();
                result = codec1.serialize(converter, result.get(), getter1.apply(element));
                if (result.isError()) return result.mapError();
                return result;
            }

            @Override
            public <S> Result<O> deserialize(DataConverter<S> converter, Map<S, S> map) {
                Result<T1> result0 = codec0.deserialize(converter, map);
                if (result0.isError()) return result0.mapError();
                Result<T2> result1 = codec1.deserialize(converter, map);
                if (result1.isError()) return result1.mapError();
                return Result.success(constructor.apply(result0.get(), result1.get()));
            }
        };
    }

    public static <T1, T2, O> Codec<O> codec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final I2<T1, T2, O> constructor
    ) {
        return mapCodec(codec0, getter0, codec1, getter1, constructor).asCodec();
    }

    public static <T1, T2, T3, O> MapCodec<O> mapCodec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final MapCodec<T3> codec2, final Function<O, T3> getter2,
            final I3<T1, T2, T3, O> constructor
    ) {
        return new MapCodec<O>() {
            @Override
            public <S> Result<Map<S, S>> serialize(DataConverter<S> converter, Map<S, S> map, O element) {
                Result<Map<S, S>> result = codec0.serialize(converter, map, getter0.apply(element));
                if (result.isError()) return result.mapError();
                result = codec1.serialize(converter, result.get(), getter1.apply(element));
                if (result.isError()) return result.mapError();
                result = codec2.serialize(converter, result.get(), getter2.apply(element));
                if (result.isError()) return result.mapError();
                return result;
            }

            @Override
            public <S> Result<O> deserialize(DataConverter<S> converter, Map<S, S> map) {
                Result<T1> result0 = codec0.deserialize(converter, map);
                if (result0.isError()) return result0.mapError();
                Result<T2> result1 = codec1.deserialize(converter, map);
                if (result1.isError()) return result1.mapError();
                Result<T3> result2 = codec2.deserialize(converter, map);
                if (result2.isError()) return result2.mapError();
                return Result.success(constructor.apply(result0.get(), result1.get(), result2.get()));
            }
        };
    }

    public static <T1, T2, T3, O> Codec<O> codec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final MapCodec<T3> codec2, final Function<O, T3> getter2,
            final I3<T1, T2, T3, O> constructor
    ) {
        return mapCodec(codec0, getter0, codec1, getter1, codec2, getter2, constructor).asCodec();
    }

    public static <T1, T2, T3, T4, O> MapCodec<O> mapCodec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final MapCodec<T3> codec2, final Function<O, T3> getter2,
            final MapCodec<T4> codec3, final Function<O, T4> getter3,
            final I4<T1, T2, T3, T4, O> constructor
    ) {
        return new MapCodec<O>() {
            @Override
            public <S> Result<Map<S, S>> serialize(DataConverter<S> converter, Map<S, S> map, O element) {
                Result<Map<S, S>> result = codec0.serialize(converter, map, getter0.apply(element));
                if (result.isError()) return result.mapError();
                result = codec1.serialize(converter, result.get(), getter1.apply(element));
                if (result.isError()) return result.mapError();
                result = codec2.serialize(converter, result.get(), getter2.apply(element));
                if (result.isError()) return result.mapError();
                result = codec3.serialize(converter, result.get(), getter3.apply(element));
                if (result.isError()) return result.mapError();
                return result;
            }

            @Override
            public <S> Result<O> deserialize(DataConverter<S> converter, Map<S, S> map) {
                Result<T1> result0 = codec0.deserialize(converter, map);
                if (result0.isError()) return result0.mapError();
                Result<T2> result1 = codec1.deserialize(converter, map);
                if (result1.isError()) return result1.mapError();
                Result<T3> result2 = codec2.deserialize(converter, map);
                if (result2.isError()) return result2.mapError();
                Result<T4> result3 = codec3.deserialize(converter, map);
                if (result3.isError()) return result3.mapError();
                return Result.success(constructor.apply(result0.get(), result1.get(), result2.get(), result3.get()));
            }
        };
    }

    public static <T1, T2, T3, T4, O> Codec<O> codec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final MapCodec<T3> codec2, final Function<O, T3> getter2,
            final MapCodec<T4> codec3, final Function<O, T4> getter3,
            final I4<T1, T2, T3, T4, O> constructor
    ) {
        return mapCodec(codec0, getter0, codec1, getter1, codec2, getter2, codec3, getter3, constructor).asCodec();
    }

    public static <T1, T2, T3, T4, T5, O> MapCodec<O> mapCodec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final MapCodec<T3> codec2, final Function<O, T3> getter2,
            final MapCodec<T4> codec3, final Function<O, T4> getter3,
            final MapCodec<T5> codec4, final Function<O, T5> getter4,
            final I5<T1, T2, T3, T4, T5, O> constructor
    ) {
        return new MapCodec<O>() {
            @Override
            public <S> Result<Map<S, S>> serialize(DataConverter<S> converter, Map<S, S> map, O element) {
                Result<Map<S, S>> result = codec0.serialize(converter, map, getter0.apply(element));
                if (result.isError()) return result.mapError();
                result = codec1.serialize(converter, result.get(), getter1.apply(element));
                if (result.isError()) return result.mapError();
                result = codec2.serialize(converter, result.get(), getter2.apply(element));
                if (result.isError()) return result.mapError();
                result = codec3.serialize(converter, result.get(), getter3.apply(element));
                if (result.isError()) return result.mapError();
                result = codec4.serialize(converter, result.get(), getter4.apply(element));
                if (result.isError()) return result.mapError();
                return result;
            }

            @Override
            public <S> Result<O> deserialize(DataConverter<S> converter, Map<S, S> map) {
                Result<T1> result0 = codec0.deserialize(converter, map);
                if (result0.isError()) return result0.mapError();
                Result<T2> result1 = codec1.deserialize(converter, map);
                if (result1.isError()) return result1.mapError();
                Result<T3> result2 = codec2.deserialize(converter, map);
                if (result2.isError()) return result2.mapError();
                Result<T4> result3 = codec3.deserialize(converter, map);
                if (result3.isError()) return result3.mapError();
                Result<T5> result4 = codec4.deserialize(converter, map);
                if (result4.isError()) return result4.mapError();
                return Result.success(constructor.apply(result0.get(), result1.get(), result2.get(), result3.get(), result4.get()));
            }
        };
    }

    public static <T1, T2, T3, T4, T5, O> Codec<O> codec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final MapCodec<T3> codec2, final Function<O, T3> getter2,
            final MapCodec<T4> codec3, final Function<O, T4> getter3,
            final MapCodec<T5> codec4, final Function<O, T5> getter4,
            final I5<T1, T2, T3, T4, T5, O> constructor
    ) {
        return mapCodec(codec0, getter0, codec1, getter1, codec2, getter2, codec3, getter3, codec4, getter4, constructor).asCodec();
    }

    public static <T1, T2, T3, T4, T5, T6, O> MapCodec<O> mapCodec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final MapCodec<T3> codec2, final Function<O, T3> getter2,
            final MapCodec<T4> codec3, final Function<O, T4> getter3,
            final MapCodec<T5> codec4, final Function<O, T5> getter4,
            final MapCodec<T6> codec5, final Function<O, T6> getter5,
            final I6<T1, T2, T3, T4, T5, T6, O> constructor
    ) {
        return new MapCodec<O>() {
            @Override
            public <S> Result<Map<S, S>> serialize(DataConverter<S> converter, Map<S, S> map, O element) {
                Result<Map<S, S>> result = codec0.serialize(converter, map, getter0.apply(element));
                if (result.isError()) return result.mapError();
                result = codec1.serialize(converter, result.get(), getter1.apply(element));
                if (result.isError()) return result.mapError();
                result = codec2.serialize(converter, result.get(), getter2.apply(element));
                if (result.isError()) return result.mapError();
                result = codec3.serialize(converter, result.get(), getter3.apply(element));
                if (result.isError()) return result.mapError();
                result = codec4.serialize(converter, result.get(), getter4.apply(element));
                if (result.isError()) return result.mapError();
                result = codec5.serialize(converter, result.get(), getter5.apply(element));
                if (result.isError()) return result.mapError();
                return result;
            }

            @Override
            public <S> Result<O> deserialize(DataConverter<S> converter, Map<S, S> map) {
                Result<T1> result0 = codec0.deserialize(converter, map);
                if (result0.isError()) return result0.mapError();
                Result<T2> result1 = codec1.deserialize(converter, map);
                if (result1.isError()) return result1.mapError();
                Result<T3> result2 = codec2.deserialize(converter, map);
                if (result2.isError()) return result2.mapError();
                Result<T4> result3 = codec3.deserialize(converter, map);
                if (result3.isError()) return result3.mapError();
                Result<T5> result4 = codec4.deserialize(converter, map);
                if (result4.isError()) return result4.mapError();
                Result<T6> result5 = codec5.deserialize(converter, map);
                if (result5.isError()) return result5.mapError();
                return Result.success(constructor.apply(result0.get(), result1.get(), result2.get(), result3.get(), result4.get(), result5.get()));
            }
        };
    }

    public static <T1, T2, T3, T4, T5, T6, O> Codec<O> codec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final MapCodec<T3> codec2, final Function<O, T3> getter2,
            final MapCodec<T4> codec3, final Function<O, T4> getter3,
            final MapCodec<T5> codec4, final Function<O, T5> getter4,
            final MapCodec<T6> codec5, final Function<O, T6> getter5,
            final I6<T1, T2, T3, T4, T5, T6, O> constructor
    ) {
        return mapCodec(codec0, getter0, codec1, getter1, codec2, getter2, codec3, getter3, codec4, getter4, codec5, getter5, constructor).asCodec();
    }

    public static <T1, T2, T3, T4, T5, T6, T7, O> MapCodec<O> mapCodec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final MapCodec<T3> codec2, final Function<O, T3> getter2,
            final MapCodec<T4> codec3, final Function<O, T4> getter3,
            final MapCodec<T5> codec4, final Function<O, T5> getter4,
            final MapCodec<T6> codec5, final Function<O, T6> getter5,
            final MapCodec<T7> codec6, final Function<O, T7> getter6,
            final I7<T1, T2, T3, T4, T5, T6, T7, O> constructor
    ) {
        return new MapCodec<O>() {
            @Override
            public <S> Result<Map<S, S>> serialize(DataConverter<S> converter, Map<S, S> map, O element) {
                Result<Map<S, S>> result = codec0.serialize(converter, map, getter0.apply(element));
                if (result.isError()) return result.mapError();
                result = codec1.serialize(converter, result.get(), getter1.apply(element));
                if (result.isError()) return result.mapError();
                result = codec2.serialize(converter, result.get(), getter2.apply(element));
                if (result.isError()) return result.mapError();
                result = codec3.serialize(converter, result.get(), getter3.apply(element));
                if (result.isError()) return result.mapError();
                result = codec4.serialize(converter, result.get(), getter4.apply(element));
                if (result.isError()) return result.mapError();
                result = codec5.serialize(converter, result.get(), getter5.apply(element));
                if (result.isError()) return result.mapError();
                result = codec6.serialize(converter, result.get(), getter6.apply(element));
                if (result.isError()) return result.mapError();
                return result;
            }

            @Override
            public <S> Result<O> deserialize(DataConverter<S> converter, Map<S, S> map) {
                Result<T1> result0 = codec0.deserialize(converter, map);
                if (result0.isError()) return result0.mapError();
                Result<T2> result1 = codec1.deserialize(converter, map);
                if (result1.isError()) return result1.mapError();
                Result<T3> result2 = codec2.deserialize(converter, map);
                if (result2.isError()) return result2.mapError();
                Result<T4> result3 = codec3.deserialize(converter, map);
                if (result3.isError()) return result3.mapError();
                Result<T5> result4 = codec4.deserialize(converter, map);
                if (result4.isError()) return result4.mapError();
                Result<T6> result5 = codec5.deserialize(converter, map);
                if (result5.isError()) return result5.mapError();
                Result<T7> result6 = codec6.deserialize(converter, map);
                if (result6.isError()) return result6.mapError();
                return Result.success(constructor.apply(result0.get(), result1.get(), result2.get(), result3.get(), result4.get(), result5.get(), result6.get()));
            }
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, O> Codec<O> codec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final MapCodec<T3> codec2, final Function<O, T3> getter2,
            final MapCodec<T4> codec3, final Function<O, T4> getter3,
            final MapCodec<T5> codec4, final Function<O, T5> getter4,
            final MapCodec<T6> codec5, final Function<O, T6> getter5,
            final MapCodec<T7> codec6, final Function<O, T7> getter6,
            final I7<T1, T2, T3, T4, T5, T6, T7, O> constructor
    ) {
        return mapCodec(codec0, getter0, codec1, getter1, codec2, getter2, codec3, getter3, codec4, getter4, codec5, getter5, codec6, getter6, constructor).asCodec();
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, O> MapCodec<O> mapCodec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final MapCodec<T3> codec2, final Function<O, T3> getter2,
            final MapCodec<T4> codec3, final Function<O, T4> getter3,
            final MapCodec<T5> codec4, final Function<O, T5> getter4,
            final MapCodec<T6> codec5, final Function<O, T6> getter5,
            final MapCodec<T7> codec6, final Function<O, T7> getter6,
            final MapCodec<T8> codec7, final Function<O, T8> getter7,
            final I8<T1, T2, T3, T4, T5, T6, T7, T8, O> constructor
    ) {
        return new MapCodec<O>() {
            @Override
            public <S> Result<Map<S, S>> serialize(DataConverter<S> converter, Map<S, S> map, O element) {
                Result<Map<S, S>> result = codec0.serialize(converter, map, getter0.apply(element));
                if (result.isError()) return result.mapError();
                result = codec1.serialize(converter, result.get(), getter1.apply(element));
                if (result.isError()) return result.mapError();
                result = codec2.serialize(converter, result.get(), getter2.apply(element));
                if (result.isError()) return result.mapError();
                result = codec3.serialize(converter, result.get(), getter3.apply(element));
                if (result.isError()) return result.mapError();
                result = codec4.serialize(converter, result.get(), getter4.apply(element));
                if (result.isError()) return result.mapError();
                result = codec5.serialize(converter, result.get(), getter5.apply(element));
                if (result.isError()) return result.mapError();
                result = codec6.serialize(converter, result.get(), getter6.apply(element));
                if (result.isError()) return result.mapError();
                result = codec7.serialize(converter, result.get(), getter7.apply(element));
                if (result.isError()) return result.mapError();
                return result;
            }

            @Override
            public <S> Result<O> deserialize(DataConverter<S> converter, Map<S, S> map) {
                Result<T1> result0 = codec0.deserialize(converter, map);
                if (result0.isError()) return result0.mapError();
                Result<T2> result1 = codec1.deserialize(converter, map);
                if (result1.isError()) return result1.mapError();
                Result<T3> result2 = codec2.deserialize(converter, map);
                if (result2.isError()) return result2.mapError();
                Result<T4> result3 = codec3.deserialize(converter, map);
                if (result3.isError()) return result3.mapError();
                Result<T5> result4 = codec4.deserialize(converter, map);
                if (result4.isError()) return result4.mapError();
                Result<T6> result5 = codec5.deserialize(converter, map);
                if (result5.isError()) return result5.mapError();
                Result<T7> result6 = codec6.deserialize(converter, map);
                if (result6.isError()) return result6.mapError();
                Result<T8> result7 = codec7.deserialize(converter, map);
                if (result7.isError()) return result7.mapError();
                return Result.success(constructor.apply(result0.get(), result1.get(), result2.get(), result3.get(), result4.get(), result5.get(), result6.get(), result7.get()));
            }
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, O> Codec<O> codec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final MapCodec<T3> codec2, final Function<O, T3> getter2,
            final MapCodec<T4> codec3, final Function<O, T4> getter3,
            final MapCodec<T5> codec4, final Function<O, T5> getter4,
            final MapCodec<T6> codec5, final Function<O, T6> getter5,
            final MapCodec<T7> codec6, final Function<O, T7> getter6,
            final MapCodec<T8> codec7, final Function<O, T8> getter7,
            final I8<T1, T2, T3, T4, T5, T6, T7, T8, O> constructor
    ) {
        return mapCodec(codec0, getter0, codec1, getter1, codec2, getter2, codec3, getter3, codec4, getter4, codec5, getter5, codec6, getter6, codec7, getter7, constructor).asCodec();
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, O> MapCodec<O> mapCodec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final MapCodec<T3> codec2, final Function<O, T3> getter2,
            final MapCodec<T4> codec3, final Function<O, T4> getter3,
            final MapCodec<T5> codec4, final Function<O, T5> getter4,
            final MapCodec<T6> codec5, final Function<O, T6> getter5,
            final MapCodec<T7> codec6, final Function<O, T7> getter6,
            final MapCodec<T8> codec7, final Function<O, T8> getter7,
            final MapCodec<T9> codec8, final Function<O, T9> getter8,
            final I9<T1, T2, T3, T4, T5, T6, T7, T8, T9, O> constructor
    ) {
        return new MapCodec<O>() {
            @Override
            public <S> Result<Map<S, S>> serialize(DataConverter<S> converter, Map<S, S> map, O element) {
                Result<Map<S, S>> result = codec0.serialize(converter, map, getter0.apply(element));
                if (result.isError()) return result.mapError();
                result = codec1.serialize(converter, result.get(), getter1.apply(element));
                if (result.isError()) return result.mapError();
                result = codec2.serialize(converter, result.get(), getter2.apply(element));
                if (result.isError()) return result.mapError();
                result = codec3.serialize(converter, result.get(), getter3.apply(element));
                if (result.isError()) return result.mapError();
                result = codec4.serialize(converter, result.get(), getter4.apply(element));
                if (result.isError()) return result.mapError();
                result = codec5.serialize(converter, result.get(), getter5.apply(element));
                if (result.isError()) return result.mapError();
                result = codec6.serialize(converter, result.get(), getter6.apply(element));
                if (result.isError()) return result.mapError();
                result = codec7.serialize(converter, result.get(), getter7.apply(element));
                if (result.isError()) return result.mapError();
                result = codec8.serialize(converter, result.get(), getter8.apply(element));
                if (result.isError()) return result.mapError();
                return result;
            }

            @Override
            public <S> Result<O> deserialize(DataConverter<S> converter, Map<S, S> map) {
                Result<T1> result0 = codec0.deserialize(converter, map);
                if (result0.isError()) return result0.mapError();
                Result<T2> result1 = codec1.deserialize(converter, map);
                if (result1.isError()) return result1.mapError();
                Result<T3> result2 = codec2.deserialize(converter, map);
                if (result2.isError()) return result2.mapError();
                Result<T4> result3 = codec3.deserialize(converter, map);
                if (result3.isError()) return result3.mapError();
                Result<T5> result4 = codec4.deserialize(converter, map);
                if (result4.isError()) return result4.mapError();
                Result<T6> result5 = codec5.deserialize(converter, map);
                if (result5.isError()) return result5.mapError();
                Result<T7> result6 = codec6.deserialize(converter, map);
                if (result6.isError()) return result6.mapError();
                Result<T8> result7 = codec7.deserialize(converter, map);
                if (result7.isError()) return result7.mapError();
                Result<T9> result8 = codec8.deserialize(converter, map);
                if (result8.isError()) return result8.mapError();
                return Result.success(constructor.apply(result0.get(), result1.get(), result2.get(), result3.get(), result4.get(), result5.get(), result6.get(), result7.get(), result8.get()));
            }
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, O> Codec<O> codec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final MapCodec<T3> codec2, final Function<O, T3> getter2,
            final MapCodec<T4> codec3, final Function<O, T4> getter3,
            final MapCodec<T5> codec4, final Function<O, T5> getter4,
            final MapCodec<T6> codec5, final Function<O, T6> getter5,
            final MapCodec<T7> codec6, final Function<O, T7> getter6,
            final MapCodec<T8> codec7, final Function<O, T8> getter7,
            final MapCodec<T9> codec8, final Function<O, T9> getter8,
            final I9<T1, T2, T3, T4, T5, T6, T7, T8, T9, O> constructor
    ) {
        return mapCodec(codec0, getter0, codec1, getter1, codec2, getter2, codec3, getter3, codec4, getter4, codec5, getter5, codec6, getter6, codec7, getter7, codec8, getter8, constructor).asCodec();
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, O> MapCodec<O> mapCodec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final MapCodec<T3> codec2, final Function<O, T3> getter2,
            final MapCodec<T4> codec3, final Function<O, T4> getter3,
            final MapCodec<T5> codec4, final Function<O, T5> getter4,
            final MapCodec<T6> codec5, final Function<O, T6> getter5,
            final MapCodec<T7> codec6, final Function<O, T7> getter6,
            final MapCodec<T8> codec7, final Function<O, T8> getter7,
            final MapCodec<T9> codec8, final Function<O, T9> getter8,
            final MapCodec<T10> codec9, final Function<O, T10> getter9,
            final I10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, O> constructor
    ) {
        return new MapCodec<O>() {
            @Override
            public <S> Result<Map<S, S>> serialize(DataConverter<S> converter, Map<S, S> map, O element) {
                Result<Map<S, S>> result = codec0.serialize(converter, map, getter0.apply(element));
                if (result.isError()) return result.mapError();
                result = codec1.serialize(converter, result.get(), getter1.apply(element));
                if (result.isError()) return result.mapError();
                result = codec2.serialize(converter, result.get(), getter2.apply(element));
                if (result.isError()) return result.mapError();
                result = codec3.serialize(converter, result.get(), getter3.apply(element));
                if (result.isError()) return result.mapError();
                result = codec4.serialize(converter, result.get(), getter4.apply(element));
                if (result.isError()) return result.mapError();
                result = codec5.serialize(converter, result.get(), getter5.apply(element));
                if (result.isError()) return result.mapError();
                result = codec6.serialize(converter, result.get(), getter6.apply(element));
                if (result.isError()) return result.mapError();
                result = codec7.serialize(converter, result.get(), getter7.apply(element));
                if (result.isError()) return result.mapError();
                result = codec8.serialize(converter, result.get(), getter8.apply(element));
                if (result.isError()) return result.mapError();
                result = codec9.serialize(converter, result.get(), getter9.apply(element));
                if (result.isError()) return result.mapError();
                return result;
            }

            @Override
            public <S> Result<O> deserialize(DataConverter<S> converter, Map<S, S> map) {
                Result<T1> result0 = codec0.deserialize(converter, map);
                if (result0.isError()) return result0.mapError();
                Result<T2> result1 = codec1.deserialize(converter, map);
                if (result1.isError()) return result1.mapError();
                Result<T3> result2 = codec2.deserialize(converter, map);
                if (result2.isError()) return result2.mapError();
                Result<T4> result3 = codec3.deserialize(converter, map);
                if (result3.isError()) return result3.mapError();
                Result<T5> result4 = codec4.deserialize(converter, map);
                if (result4.isError()) return result4.mapError();
                Result<T6> result5 = codec5.deserialize(converter, map);
                if (result5.isError()) return result5.mapError();
                Result<T7> result6 = codec6.deserialize(converter, map);
                if (result6.isError()) return result6.mapError();
                Result<T8> result7 = codec7.deserialize(converter, map);
                if (result7.isError()) return result7.mapError();
                Result<T9> result8 = codec8.deserialize(converter, map);
                if (result8.isError()) return result8.mapError();
                Result<T10> result9 = codec9.deserialize(converter, map);
                if (result9.isError()) return result9.mapError();
                return Result.success(constructor.apply(result0.get(), result1.get(), result2.get(), result3.get(), result4.get(), result5.get(), result6.get(), result7.get(), result8.get(), result9.get()));
            }
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, O> Codec<O> codec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final MapCodec<T3> codec2, final Function<O, T3> getter2,
            final MapCodec<T4> codec3, final Function<O, T4> getter3,
            final MapCodec<T5> codec4, final Function<O, T5> getter4,
            final MapCodec<T6> codec5, final Function<O, T6> getter5,
            final MapCodec<T7> codec6, final Function<O, T7> getter6,
            final MapCodec<T8> codec7, final Function<O, T8> getter7,
            final MapCodec<T9> codec8, final Function<O, T9> getter8,
            final MapCodec<T10> codec9, final Function<O, T10> getter9,
            final I10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, O> constructor
    ) {
        return mapCodec(codec0, getter0, codec1, getter1, codec2, getter2, codec3, getter3, codec4, getter4, codec5, getter5, codec6, getter6, codec7, getter7, codec8, getter8, codec9, getter9, constructor).asCodec();
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, O> MapCodec<O> mapCodec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final MapCodec<T3> codec2, final Function<O, T3> getter2,
            final MapCodec<T4> codec3, final Function<O, T4> getter3,
            final MapCodec<T5> codec4, final Function<O, T5> getter4,
            final MapCodec<T6> codec5, final Function<O, T6> getter5,
            final MapCodec<T7> codec6, final Function<O, T7> getter6,
            final MapCodec<T8> codec7, final Function<O, T8> getter7,
            final MapCodec<T9> codec8, final Function<O, T9> getter8,
            final MapCodec<T10> codec9, final Function<O, T10> getter9,
            final MapCodec<T11> codec10, final Function<O, T11> getter10,
            final I11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, O> constructor
    ) {
        return new MapCodec<O>() {
            @Override
            public <S> Result<Map<S, S>> serialize(DataConverter<S> converter, Map<S, S> map, O element) {
                Result<Map<S, S>> result = codec0.serialize(converter, map, getter0.apply(element));
                if (result.isError()) return result.mapError();
                result = codec1.serialize(converter, result.get(), getter1.apply(element));
                if (result.isError()) return result.mapError();
                result = codec2.serialize(converter, result.get(), getter2.apply(element));
                if (result.isError()) return result.mapError();
                result = codec3.serialize(converter, result.get(), getter3.apply(element));
                if (result.isError()) return result.mapError();
                result = codec4.serialize(converter, result.get(), getter4.apply(element));
                if (result.isError()) return result.mapError();
                result = codec5.serialize(converter, result.get(), getter5.apply(element));
                if (result.isError()) return result.mapError();
                result = codec6.serialize(converter, result.get(), getter6.apply(element));
                if (result.isError()) return result.mapError();
                result = codec7.serialize(converter, result.get(), getter7.apply(element));
                if (result.isError()) return result.mapError();
                result = codec8.serialize(converter, result.get(), getter8.apply(element));
                if (result.isError()) return result.mapError();
                result = codec9.serialize(converter, result.get(), getter9.apply(element));
                if (result.isError()) return result.mapError();
                result = codec10.serialize(converter, result.get(), getter10.apply(element));
                if (result.isError()) return result.mapError();
                return result;
            }

            @Override
            public <S> Result<O> deserialize(DataConverter<S> converter, Map<S, S> map) {
                Result<T1> result0 = codec0.deserialize(converter, map);
                if (result0.isError()) return result0.mapError();
                Result<T2> result1 = codec1.deserialize(converter, map);
                if (result1.isError()) return result1.mapError();
                Result<T3> result2 = codec2.deserialize(converter, map);
                if (result2.isError()) return result2.mapError();
                Result<T4> result3 = codec3.deserialize(converter, map);
                if (result3.isError()) return result3.mapError();
                Result<T5> result4 = codec4.deserialize(converter, map);
                if (result4.isError()) return result4.mapError();
                Result<T6> result5 = codec5.deserialize(converter, map);
                if (result5.isError()) return result5.mapError();
                Result<T7> result6 = codec6.deserialize(converter, map);
                if (result6.isError()) return result6.mapError();
                Result<T8> result7 = codec7.deserialize(converter, map);
                if (result7.isError()) return result7.mapError();
                Result<T9> result8 = codec8.deserialize(converter, map);
                if (result8.isError()) return result8.mapError();
                Result<T10> result9 = codec9.deserialize(converter, map);
                if (result9.isError()) return result9.mapError();
                Result<T11> result10 = codec10.deserialize(converter, map);
                if (result10.isError()) return result10.mapError();
                return Result.success(constructor.apply(result0.get(), result1.get(), result2.get(), result3.get(), result4.get(), result5.get(), result6.get(), result7.get(), result8.get(), result9.get(), result10.get()));
            }
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, O> Codec<O> codec(
            final MapCodec<T1> codec0, final Function<O, T1> getter0,
            final MapCodec<T2> codec1, final Function<O, T2> getter1,
            final MapCodec<T3> codec2, final Function<O, T3> getter2,
            final MapCodec<T4> codec3, final Function<O, T4> getter3,
            final MapCodec<T5> codec4, final Function<O, T5> getter4,
            final MapCodec<T6> codec5, final Function<O, T6> getter5,
            final MapCodec<T7> codec6, final Function<O, T7> getter6,
            final MapCodec<T8> codec7, final Function<O, T8> getter7,
            final MapCodec<T9> codec8, final Function<O, T9> getter8,
            final MapCodec<T10> codec9, final Function<O, T10> getter9,
            final MapCodec<T11> codec10, final Function<O, T11> getter10,
            final I11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, O> constructor
    ) {
        return mapCodec(codec0, getter0, codec1, getter1, codec2, getter2, codec3, getter3, codec4, getter4, codec5, getter5, codec6, getter6, codec7, getter7, codec8, getter8, codec9, getter9, codec10, getter10, constructor).asCodec();
    }

    @FunctionalInterface
    public interface I1<T1, O> {
        O apply(final T1 t1);
    }

    @FunctionalInterface
    public interface I2<T1, T2, O> {
        O apply(final T1 t1, final T2 t2);
    }

    @FunctionalInterface
    public interface I3<T1, T2, T3, O> {
        O apply(final T1 t1, final T2 t2, final T3 t3);
    }

    @FunctionalInterface
    public interface I4<T1, T2, T3, T4, O> {
        O apply(final T1 t1, final T2 t2, final T3 t3, final T4 t4);
    }

    @FunctionalInterface
    public interface I5<T1, T2, T3, T4, T5, O> {
        O apply(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5);
    }

    @FunctionalInterface
    public interface I6<T1, T2, T3, T4, T5, T6, O> {
        O apply(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6);
    }

    @FunctionalInterface
    public interface I7<T1, T2, T3, T4, T5, T6, T7, O> {
        O apply(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6, final T7 t7);
    }

    @FunctionalInterface
    public interface I8<T1, T2, T3, T4, T5, T6, T7, T8, O> {
        O apply(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6, final T7 t7, final T8 t8);
    }

    @FunctionalInterface
    public interface I9<T1, T2, T3, T4, T5, T6, T7, T8, T9, O> {
        O apply(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6, final T7 t7, final T8 t8, final T9 t9);
    }

    @FunctionalInterface
    public interface I10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, O> {
        O apply(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6, final T7 t7, final T8 t8, final T9 t9, final T10 t10);
    }

    @FunctionalInterface
    public interface I11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, O> {
        O apply(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6, final T7 t7, final T8 t8, final T9 t9, final T10 t10, final T11 t11);
    }
}
