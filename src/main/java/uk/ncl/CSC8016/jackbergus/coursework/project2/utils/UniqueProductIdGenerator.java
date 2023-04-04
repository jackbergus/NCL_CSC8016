package uk.ncl.CSC8016.jackbergus.coursework.project2.utils;

import uk.ncl.CSC8016.jackbergus.coursework.project1.utils.AtomicBigInteger;

import java.math.BigInteger;

/**
 * Utility class generating an unique product ID per
 */
public class UniqueProductIdGenerator {

    private AtomicBigInteger unique_product_id_generator;
    private UniqueProductIdGenerator() {
        unique_product_id_generator = new AtomicBigInteger(BigInteger.ONE);
    }

    private static UniqueProductIdGenerator self = null;
    public static UniqueProductIdGenerator getInstance() {
        if (self == null) {
            self = new UniqueProductIdGenerator();
        }
        return self;
    }

    public static BigInteger nextProductId() {
        return getInstance().unique_product_id_generator.incrementAndGet();
    }

}
