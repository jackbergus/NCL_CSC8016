package uk.ncl.CSC8016.jackbergus.coursework.project2.testing;

import uk.ncl.CSC8016.jackbergus.coursework.project2.processes.RainforestShop;
import uk.ncl.CSC8016.jackbergus.coursework.project2.processes.Transaction;
import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.Item;
import uk.ncl.CSC8016.jackbergus.slides.semaphores.scheduler.Pair;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Testing2 {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public static Function<Boolean,List<Message>> test_12 = (Boolean isGlobalLock) -> null;

    public static Function<Boolean,List<Message>> test_11 = (Boolean isGlobalLock) -> {
        List<Message> first_test = new ArrayList<>();
        List<String> ofUserNames = new ArrayList<>();
        ofUserNames.add("user1");
        ofUserNames.add("user2");
        Map<String, Pair<Double, Integer>> m = new HashMap<>();
        m.put("product", new Pair<>(1.0, 3));

        /// TEST 1) A user cannot shelf a product if nothing was basketed
        {
            RainforestShop s = new RainforestShop(ofUserNames, m, isGlobalLock);
            Optional<Transaction> l = s.login("user2");
            if (l.isPresent()) {
                var transaction = l.get();

                if (!transaction.shelfProduct(new Item("product", 1.0, BigInteger.TEN))) {
                    first_test.add(new Message(true, "GOOD (1): cannot shelf a product that doesn't exist!"));
                } else {
                    first_test.add(new Message(false, "ERROR (1): shelving an unexistant product!"));
                }

                if (!transaction.shelfProduct(new Item("product", 1.0, BigInteger.ONE))) {
                    first_test.add(new Message(true, "GOOD (2): cannot shelf a product that wasn't basketed (no basketing)!"));
                } else {
                    first_test.add(new Message(false, "ERROR (2): shelving a product that wasn't basketed (no basketing)!"));
                }

                transaction.basketProduct("product");
                Item i = transaction.getUnmutableBasket().get(0);
                if (!transaction.shelfProduct(new Item("product", 1.0, BigInteger.TEN.add(i.id)))) {
                    first_test.add(new Message(true, "GOOD (3): cannot shelf a product that wasn't basketed (with basketing)!"));
                } else {
                    first_test.add(new Message(false, "ERROR (3): shelving a product that wasn't basketed (with basketing)!"));
                }

                if (transaction.shelfProduct(i)) {
                    first_test.add(new Message(true, "GOOD (4): can shelf a product that was basketed (with basketing)!"));
                } else {
                    first_test.add(new Message(false, "ERROR (4): not shelving a product that was basketed (with basketing)!"));
                }
            }
        }

        return first_test;
    };
    public static Function<Boolean,List<Message>> test_01 = (Boolean isGlobalLock) -> {
        List<Message> first_test = new ArrayList<>();
        List<String> ofUserNames = new ArrayList<>();
        ofUserNames.add("bogus");

        /// TEST 1) server with no users
        {
            RainforestShop s = new RainforestShop(Collections.emptyList(), Collections.emptyMap(), isGlobalLock);
            try {
                var result = s.login("bogus");
                if (result.isPresent()) {
                    var transaction = result.get();
                    first_test.add(new Message(false, "ERROR (1): you cannot log-in a user which is not in the server"));
                    transaction.logout();
                } else {
                    first_test.add(new Message(true, "GOOD (1): you cannot start a transaction if a user is not listed!"));

                }
            } catch (Exception e) {
                first_test.add(new Message(false, e.getMessage()+(Thread.currentThread().getStackTrace())));
            }
        }

        /// TEST 3) server with one user, wrong log-in
        {
            RainforestShop s = new RainforestShop(ofUserNames, Collections.emptyMap(), isGlobalLock);
            try {
                var result = s.login("boguso");
                if (result.isPresent()) {
                    var transaction = result.get();
                    first_test.add(new Message(false, "ERROR (3): you cannot log-in a user which is not in the server"));
                    transaction.logout();
                } else {
                    first_test.add(new Message(true, "GOOD (3): you cannot start a transaction if a user is not listed!"));

                }
            } catch (Exception e) {
                first_test.add(new Message(false, e.getMessage()+(Thread.currentThread().getStackTrace())));
            }
        }

        return first_test;
    };

    public static Function<Boolean,List<Message>> test_02 = (Boolean isGlobalLock) -> {
        List<Message> first_test = new ArrayList<>();
        List<String> ofUserNames = new ArrayList<>();
        ofUserNames.add("bogus");
        /// TEST 2) server with one user
        {
            RainforestShop s = new RainforestShop(ofUserNames, Collections.emptyMap(), isGlobalLock);
            try {
                var result = s.login("bogus");
                if (!result.isPresent()) {
                    first_test.add(new Message(false, "ERROR (2): you should log-in a user which is in the server"));
                } else {
                    var transaction = result.get();
                    first_test.add(new Message(true, "GOOD (2): you start a transaction if a user is not listed!"));
                    transaction.logout();
                }
            } catch (Exception e) {
                first_test.add(new Message(false, e.getMessage()+(Thread.currentThread().getStackTrace())));
            }
        }
        return first_test;
    };

    public static Function<Boolean,List<Message>> test_01bis = (Boolean isGlobalLock) -> {
        List<Message> first_test = new ArrayList<>();
        List<String> ofUserNames = new ArrayList<>();
        ofUserNames.add("bogus");
        /// TEST 2) server with one user
        {
            RainforestShop s = new RainforestShop(ofUserNames, Collections.emptyMap(), isGlobalLock);
            try {
                var result = s.login("bogus");
                if (!result.isPresent()) {
                    first_test.add(new Message(false, "ERROR (1): you should log-in a user which is in the server"));
                } else {
                    var transaction = result.get();
                    if (transaction.logout()) {
                        first_test.add(new Message(true, "GOOD (1): you start a transaction if a user is not listed!"));
                        if (!transaction.logout()) {
                            first_test.add(new Message(true, "GOOD (2): you cannot log-out multiple times!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (2): logging out a second time should return false!"));
                        }
                    } else {
                        first_test.add(new Message(false, "ERROR (1): expecting that the transaction returns true!"));
                        if (!transaction.logout()) {
                            first_test.add(new Message(true, "WARNING (2): you cannot log-out multiple times!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (2): logging out a second time should return false!"));
                        }
                    }
                }
            } catch (Exception e) {
                first_test.add(new Message(false, e.getMessage()+(Thread.currentThread().getStackTrace())));
            }
        }

        {
            RainforestShop s = new RainforestShop(ofUserNames, Collections.emptyMap(), isGlobalLock);
            try {
                var result = s.login("bogus");
                if (!result.isPresent()) {
                    first_test.add(new Message(false, "ERROR (1): you should log-in a user which is in the server"));
                    first_test.add(new Message(false, "ERROR (2): this also entails that you cannot re-login"));
                } else {
                    var transaction = result.get();
                    String initialId = transaction.getUuid().toString();
                    transaction.logout();

                    result = s.login("bogus");
                    if (!result.isPresent()) {
                        first_test.add(new Message(false, "ERROR (3): you should be able to re-log-in!"));
                        first_test.add(new Message(false, "ERROR (4): therefore, cannot test whether the transaction has a different ID!"));
                    } else {
                        first_test.add(new Message(true, "GOOD (3): you can re-log-in!"));
                        result.ifPresent(transaction2 -> {
                            if (!transaction2.getUuid().toString().equals(initialId)) {
                                first_test.add(new Message(true, "GOOD (4): the novel transaction has a novel ID!"));
                            } else {

                                first_test.add(new Message(false, "ERROR (4): the novel transaction shall also have a novel ID!"));
                            }
                        });
                    }
                }
            } catch (Exception e) {
                first_test.add(new Message(false, e.getMessage()+(Thread.currentThread().getStackTrace())));
            }
        }
        return first_test;
    };

    public static Function<Boolean,List<Message>> test_03 = (Boolean isGlobalLock) -> {
        return null;
    };


    public static Function<Boolean,List<Message>> test_04 = (Boolean isGlobalLock) -> {
        List<Message> first_test = new ArrayList<>();
        List<String> ofUserNames = new ArrayList<>();
        ofUserNames.add("bogus");
        /// TEST 1) Cannot basket unavailable product
        {
            RainforestShop s = new RainforestShop(ofUserNames, Collections.emptyMap(), isGlobalLock);
            try {
                var result1 = s.login("bogus");
                result1.ifPresent(transaction -> {
                    if (transaction.basketProduct("bogus")) {
                        first_test.add(new Message(false, "ERROR (1a): I must not basket unavailable products!"));
                    } else {
                        first_test.add(new Message(true, "GOOD (1a): you cannot log-in multiple times!"));
                    }
                    transaction.logout();
                });
            } catch (Exception e) {
                first_test.add(new Message(false, e.getMessage()+(Arrays.stream(Thread.currentThread().getStackTrace()).map(x->x.toString()).collect(Collectors.joining("; ")))));
            }
        }

        /// TEST 2) Cannot basket more than the provided quantity
        {
            Map<String, Pair<Double, Integer>> m = new HashMap<>();
            m.put("product", new Pair<>(1.0, 1));
            RainforestShop s = new RainforestShop(ofUserNames, m, isGlobalLock);
            try {
                var result1 = s.login("bogus");
                result1.ifPresent(transaction -> {
                    if (transaction.basketProduct("bogus")) {
                        first_test.add(new Message(false, "ERROR (1b): I must not basket unavailable products!"));
                    } else {
                        first_test.add(new Message(true, "GOOD (b1): you cannot basket unavailable products!"));
                    }
                    if (!transaction.basketProduct("product")) {
                        first_test.add(new Message(false, "ERROR (2b): I must basket available products!"));
                    } else {
                        first_test.add(new Message(true, "GOOD (2b): you did not basked an available product!"));
                    }
                    transaction.logout();
                });
            } catch (Exception e) {
                first_test.add(new Message(false, e.getMessage()+(Arrays.stream(Thread.currentThread().getStackTrace()).map(x->x.toString()).collect(Collectors.joining("; ")))));
            }
        }

        /// TEST 3) Cannot basket more than the provided quantity
        {
            Map<String, Pair<Double, Integer>> m = new HashMap<>();
            m.put("product", new Pair<>(1.0, 1));
            RainforestShop s = new RainforestShop(ofUserNames, m, isGlobalLock);
            try {
                var result1 = s.login("bogus");
                result1.ifPresent(transaction -> {
                    if (!transaction.basketProduct("product")) {
                        first_test.add(new Message(false, "ERROR (1c): I must basket available products!"));
                    } else {
                        first_test.add(new Message(true, "GOOD (1c): you did not basked an available product!"));
                    }
                    if (!transaction.shelfProduct(transaction.getUnmutableBasket().get(0))) {
                        first_test.add(new Message(false, "ERROR (2c): I must be able to shelf a product that I basketed!"));
                    } else {
                        first_test.add(new Message(true, "GOOD (2c): I can correctly shelf a product that I previously basketed!"));
                    }
                    if (!transaction.basketProduct("product")) {
                        first_test.add(new Message(false, "ERROR (3c): I must basket available products that have been put back in the shelf!"));
                    } else {
                        first_test.add(new Message(true, "GOOD (3c): you did not basked an available product that was put back on the shelf!"));
                    }
                    if (transaction.basketProduct("product")) {
                        first_test.add(new Message(false, "ERROR (4c): I shall not be able to basket a single shelfed prouct multiple times!"));
                    } else {
                        first_test.add(new Message(true, "GOOD (4c): you did not basketed a previously shelfed product more than once"));
                    }
                    transaction.logout();
                });
            } catch (Exception e) {
                first_test.add(new Message(false, e.getMessage()+(Arrays.stream(Thread.currentThread().getStackTrace()).map(x->x.toString()).collect(Collectors.joining("; ")))));
            }
        }

        return first_test;
    };

    public static Function<Boolean,List<Message>> test_05 = (Boolean isGlobalLock) -> {
        List<Message> first_test = new ArrayList<>();
        List<String> ofUserNames = new ArrayList<>();
        ofUserNames.add("bogus");
        {
            Map<String, Pair<Double, Integer>> m = new HashMap<>();
            m.put("product", new Pair<>(1.0, 1));
            RainforestShop s = new RainforestShop(ofUserNames, m, isGlobalLock);
            try {
                var result1 = s.login("bogus");
                result1.ifPresent(transaction -> {
                    transaction.basketProduct("product");
                    transaction.logout();
                });
                result1 = s.login("bogus");
                result1.ifPresent(transaction -> {
                    if (!transaction.basketProduct("product")) {
                        first_test.add(new Message(false, "ERROR (1a): I must be able to basket a product that was not bought at log-out!"));
                    } else {
                        first_test.add(new Message(true, "GOOD (1a): you did basket a product that was automatically re-selved at logout!"));
                    }
                });
            } catch (Exception e) {
                first_test.add(new Message(false, e.getMessage()+(Arrays.stream(Thread.currentThread().getStackTrace()).map(x->x.toString()).collect(Collectors.joining("; ")))));
            }
        }
        return first_test;
    };

    public static Function<Boolean,List<Message>> test_05bis = (Boolean isGlobalLock) -> {
        List<Message> first_test = new ArrayList<>();
        List<String> ofUserNames = new ArrayList<>();
        ofUserNames.add("bogus");
        {
            Map<String, Pair<Double, Integer>> m = new HashMap<>();
            m.put("product", new Pair<>(1.0, 1));
            RainforestShop s = new RainforestShop(ofUserNames, m, isGlobalLock);
            try {
                var result1 = s.login("bogus");
                result1.ifPresent(transaction -> {
                    transaction.basketProduct("product");
                    transaction.logout();

                    if (transaction.basketProduct("product")) {
                        first_test.add(new Message(false, "ERROR (1): cannot basket a product after logging out with the same transaction!"));
                    } else {
                        first_test.add(new Message(true, "GOOD (1): cannot basket a product after logging out with the same transaction!"));
                    }

                    if (transaction.shelfProduct(new Item("product", 1.0, null))) {
                        first_test.add(new Message(false, "ERROR (2): cannot shelf a product after logging out with the same transaction!"));
                    } else {
                        first_test.add(new Message(true, "GOOD (2): cannot shelf a product after logging out with the same transaction!"));
                    }

                    if (transaction.basketCheckout(Double.MAX_VALUE) != null) {
                        first_test.add(new Message(false, "ERROR (3): cannot provide a checkout/basket information if the transaction was not legit!"));
                    } else {
                        first_test.add(new Message(true, "GOOD (3): cannot provide a checkout/basket information if the transaction was not legit"));
                    }
                });
            } catch (Exception e) {
                first_test.add(new Message(false, e.getMessage()+(Arrays.stream(Thread.currentThread().getStackTrace()).map(x->x.toString()).collect(Collectors.joining("; ")))));
            }
        }
        return first_test;
    };


    public static Function<Boolean,List<Message>> test_06 = (Boolean isGlobalLock) -> {
        List<Message> first_test = new ArrayList<>();
        List<String> ofUserNames = new ArrayList<>();
        ofUserNames.add("bogus");

        // 1) It should be possible to basket checkout when the basket is empty, with any given amount of money
        {
            Map<String, Pair<Double, Integer>> m = new HashMap<>();
            m.put("product", new Pair<>(1.0, 1));
            RainforestShop s = new RainforestShop(ofUserNames, m, isGlobalLock);
            try {
                var result1 = s.login("bogus");
                result1.ifPresent(transaction -> {
                    double pecunia = new Random().nextDouble();
                    var transResult = transaction.basketCheckout(pecunia);
                    if (transResult == null) {
                        first_test.add(new Message(false, "ERROR (1a-6a): No basket information was returend; so, I cannot test the basket's values!"));
                    } else {
                        first_test.add(new Message(true, "GOOD (1a): A basket information was returend!"));
                        if (transResult.total_cost == 0.0) {
                            first_test.add(new Message(true, "GOOD (2a): The cost of an empty basket is zero!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (2a): the cost of an empty basket shall be zero!"));
                        }
                        if (transResult.total_given == pecunia) {
                            first_test.add(new Message(true, "GOOD (3a): The given money should be equal to the random-generated value"));
                        } else {
                            first_test.add(new Message(false, "ERROR (3a): The given money should be equal to the random-generated value!"));
                        }
                        if (transResult.account_result == pecunia) {
                            first_test.add(new Message(true, "GOOD (4a): Given that nothing was bought, the amount of money shall stay the same!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (4a): Given that nothing was bought, the amount of money shall stay the same!"));
                        }
                        if (transResult.boughtItems.isEmpty()) {
                            first_test.add(new Message(true, "GOOD (5a): The available items shall be empty!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (5a): The available items shall be empty!"));
                        }
                        if (transResult.unavailableItems.isEmpty()) {
                            first_test.add(new Message(true, "GOOD (6a): No unavailable shall be there!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (6a): No unavailable shall be there!  (These shall be non-zero mainly when the money is available but the products are no more available, as another user purchased those before you!)"));
                        }
                    }
                    transaction.logout();
                });
            } catch (Exception e) {
                first_test.add(new Message(false, e.getMessage()+(Arrays.stream(Thread.currentThread().getStackTrace()).map(x->x.toString()).collect(Collectors.joining("; ")))));
            }
        }

        // 2) Correct basket information when purchasing one item
        {
            Map<String, Pair<Double, Integer>> m = new HashMap<>();
            m.put("product", new Pair<>(1.0, 1));
            RainforestShop s = new RainforestShop(ofUserNames, m, isGlobalLock);
            try {
                var result1 = s.login("bogus");
                result1.ifPresent(transaction -> {
                    double pecunia = Math.abs(new Random().nextDouble()); // Random positive number
                    transaction.basketProduct("product");
                    var transResult = transaction.basketCheckout(pecunia+1.0);
                    if (transResult == null) {
                        first_test.add(new Message(false, "ERROR (1b-6b): No basket information was returend; so, I cannot test the basket's values!"));
                    } else {
                        first_test.add(new Message(true, "GOOD (1b): A basket information was returend!"));
                        if (transResult.total_cost == 1.0) {
                            first_test.add(new Message(true, "GOOD (2b): The cost matches the one of the product!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (2b): The cost should match the one of the product!"));
                        }
                        if (transResult.total_given == pecunia+1.0) {
                            first_test.add(new Message(true, "GOOD (3b): The given money should be equal to the random number plus the cost of the product"));
                        } else {
                            first_test.add(new Message(false, "ERROR (3b): The given money should be equal to the random number plus the cost of the product!"));
                        }
                        if (transResult.account_result == ( pecunia+1.0)-1.0) {
                            first_test.add(new Message(true, "GOOD (4b): The remaining value should be equivalent to the random number!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (4b): The remaining value should be equivalent to the random number!"));
                        }
                        if (transResult.boughtItems.size() == 1) {
                            first_test.add(new Message(true, "GOOD (5b): The available items shall contain 1 item!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (5b): The available items shall contain 1 item!"));
                        }
                        if (transResult.unavailableItems.isEmpty()) {
                            first_test.add(new Message(true, "GOOD (6b): No unavailable shall be there!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (6b): No unavailable shall be there!  (These shall be non-zero mainly when the money is available but the products are no more available, as another user purchased those before you!)"));
                        }
                    }
                    transaction.logout();
                });
            } catch (Exception e) {
                first_test.add(new Message(false, e.getMessage()+(Arrays.stream(Thread.currentThread().getStackTrace()).map(x->x.toString()).collect(Collectors.joining("; ")))));
            }
        }

        // 3) Correct basket information when purchasing two items
        {
            Map<String, Pair<Double, Integer>> m = new HashMap<>();
            m.put("product", new Pair<>(1.0, 2));
            RainforestShop s = new RainforestShop(ofUserNames, m, isGlobalLock);
            try {
                var result1 = s.login("bogus");
                result1.ifPresent(transaction -> {
                    double pecunia = Math.abs(new Random().nextDouble()); // Random positive number
                    transaction.basketProduct("product");
                    transaction.basketProduct("product");
                    var transResult = transaction.basketCheckout(pecunia+2.0);
                    if (transResult == null) {
                        first_test.add(new Message(false, "ERROR (1c-6c): No basket information was returend; so, I cannot test the basket's values!"));
                    } else {
                        first_test.add(new Message(true, "GOOD (1c): A basket information was returend!"));
                        if (transResult.total_cost == 2.0) {
                            first_test.add(new Message(true, "GOOD (2c): The cost matches the one of the product!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (2c): The cost should match the one of the product!"));
                        }
                        if (transResult.total_given == pecunia+2.0) {
                            first_test.add(new Message(true, "GOOD (3c): The given money should be equal to the random number plus the cost of the product"));
                        } else {
                            first_test.add(new Message(false, "ERROR (3c): The given money should be equal to the random number plus the cost of the product!"));
                        }
                        if (transResult.account_result == ( pecunia+2.0)-2.0) {
                            first_test.add(new Message(true, "GOOD (4c): The remaining value should be equivalent to the random number!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (4c): The remaining value should be equivalent to the random number!"));
                        }
                        if (transResult.boughtItems.size() == 2) {
                            first_test.add(new Message(true, "GOOD (5c): The available items shall contain 2 items!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (5c): The available items shall contain 2 item!"));
                        }
                        if (transResult.unavailableItems.isEmpty()) {
                            first_test.add(new Message(true, "GOOD (6c): No unavailable shall be there!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (6c): No unavailable shall be there!  (These shall be non-zero mainly when the money is available but the products are no more available, as another user purchased those before you!)"));
                        }
                    }
                    transaction.logout();
                });
            } catch (Exception e) {
                first_test.add(new Message(false, e.getMessage()+(Arrays.stream(Thread.currentThread().getStackTrace()).map(x->x.toString()).collect(Collectors.joining("; ")))));
            }
        }

        // 4) Correct basket information when attempting to purchase three items when only two can be purchased (not enough products)
        {
            Map<String, Pair<Double, Integer>> m = new HashMap<>();
            m.put("product", new Pair<>(1.0, 2));
            RainforestShop s = new RainforestShop(ofUserNames, m, isGlobalLock);
            try {
                var result1 = s.login("bogus");
                result1.ifPresent(transaction -> {
                    double pecunia = Math.abs(new Random().nextDouble()); // Random positive number
                    transaction.basketProduct("product");
                    transaction.basketProduct("product");
                    transaction.basketProduct("product");
                    var transResult = transaction.basketCheckout(pecunia+3.0);
                    if (transResult == null) {
                        first_test.add(new Message(false, "ERROR (1d-6d): No basket information was returend; so, I cannot test the basket's values!"));
                    } else {
                        first_test.add(new Message(true, "GOOD (1d): A basket information was returend!"));
                        if (transResult.total_cost == 2.0) {
                            first_test.add(new Message(true, "GOOD (2d): The cost matches the one of the product!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (2d): The cost should match the one of the product!"));
                        }
                        if (transResult.total_given == pecunia+3.0) {
                            first_test.add(new Message(true, "GOOD (3d): The given money should be equal to the random number plus the cost of the product"));
                        } else {
                            first_test.add(new Message(false, "ERROR (3d): The given money should be equal to the random number plus the cost of the product!"));
                        }
                        if (transResult.account_result == ( pecunia+3.0)-2.0) {
                            first_test.add(new Message(true, "GOOD (4d): The remaining value should be equivalent to the random number!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (4d): The remaining value should be equivalent to the random number!"));
                        }
                        if (transResult.boughtItems.size() == 2) {
                            first_test.add(new Message(true, "GOOD (5d): The available items shall contain 2 items!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (5d): The available items shall contain 2 item!"));
                        }
                        if (transResult.unavailableItems.size() == 0) {
                            first_test.add(new Message(true, "GOOD (6d): 0 unavailable shall be there!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (6d): 0 unavailable shall be there! (These shall be non-zero mainly when the money is available but the products are no more available, as another user purchased those before you!)"));
                        }
                    }
                    transaction.logout();
                });
            } catch (Exception e) {
                first_test.add(new Message(false, e.getMessage()+(Arrays.stream(Thread.currentThread().getStackTrace()).map(x->x.toString()).collect(Collectors.joining("; ")))));
            }
        }

        // 4) Correct basket information when attempting to purchase three items when only two can be purchased (not enough money)
        {
            Map<String, Pair<Double, Integer>> m = new HashMap<>();
            m.put("product", new Pair<>(1.0, 3));
            RainforestShop s = new RainforestShop(ofUserNames, m, isGlobalLock);
            try {
                var result1 = s.login("bogus");
                result1.ifPresent(transaction -> {
                    double pecunia = Math.abs(new Random().nextDouble()); // Random positive number
                    transaction.basketProduct("product");
                    transaction.basketProduct("product");
                    transaction.basketProduct("product");
                    var transResult = transaction.basketCheckout(2.0);
                    if (transResult == null) {
                        first_test.add(new Message(false, "ERROR (1e-6e): No basket information was returend; so, I cannot test the basket's values!"));
                    } else {
                        first_test.add(new Message(true, "GOOD (1e): A basket information was returend!"));
                        if (transResult.total_cost == 0.0) {
                            first_test.add(new Message(true, "GOOD (2e): The cost matches the one of the product!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (2e): The cost should match the one of the product!"));
                        }
                        if (transResult.total_given == 2.0) {
                            first_test.add(new Message(true, "GOOD (3e): The given money should be equal to purchasing two items"));
                        } else {
                            first_test.add(new Message(false, "ERROR (3e): The given money should be equal to purchasing two items"));
                        }
                        if (transResult.account_result == 2.0) {
                            first_test.add(new Message(true, "GOOD (4e): The remaining value should be equivalent to the total money!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (4e): The remaining value should be equivalent to the total money!"));
                        }
                        if (transResult.boughtItems.size() == 0) {
                            first_test.add(new Message(true, "GOOD (5e): The available items shall contain 0 items!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (5e): The available items shall be zero, as nothing should have been purchased!"));
                        }
                        if (transResult.unavailableItems.size() == 0) {
                            first_test.add(new Message(true, "GOOD (6e): 0 unavailable are there!"));
                        } else {
                            first_test.add(new Message(false, "ERROR (6e): 0 unavailable shall be there, as the items are available but they could have not been purchaes!  (These shall be non-zero mainly when the money is available but the products are no more available, as another user purchased those before you!)"));
                        }
                    }
                    transaction.logout();
                });
            } catch (Exception e) {
                first_test.add(new Message(false, e.getMessage()+(Arrays.stream(Thread.currentThread().getStackTrace()).map(x->x.toString()).collect(Collectors.joining("; ")))));
            }
        }
        return first_test;
    };

    public static Function<Boolean,List<Message>> test_07 = (Boolean isGlobalLock) -> null;

    public static Function<Boolean,List<Message>> test_08 = (Boolean isGlobalLock) -> null;

    public static Function<Boolean,List<Message>> test_09 = (Boolean isGlobalLock) -> null;

    public static Function<Boolean,List<Message>> test_10 = (Boolean isGlobalLock) -> null;


    public static boolean isGlobal = true;
    public static double total_score = 0.0;
    public static double total_max_score = 0.0;


    public static void FunctionScoring(List<Test> scoring) {
        for (var x : scoring) {
            var result = x.test.apply(isGlobal);
            double score = sumUpOk(result) * x.max_score;
            total_max_score += x.max_score;
            System.out.println(" * " + x.name + "[#"+result.size()+"]. Score = "+score);
            for (var res : result)
                System.out.println("   - " + res);
            total_score += score;
        }
    }

    public static void main(String args[]) {
        List<Test> scoring = new ArrayList<>();
        String StudentId = new RainforestShop(null, null, false).studentId();
        System.out.println("StudentId: " + StudentId);
        if (args.length > 0) {
            isGlobal = args[0].equals("global");
        }

        System.out.println("I. Single Threaded Correctness");
        System.out.println("==============================");
        System.out.println("");
        scoring.add(new Test(test_01,
                "I cannot open a transaction if the user does not appear in the initialization map.",
                4.0));
        scoring.add(new Test(test_02,
                "I can always open a transaction if the user, on the other hand, appears on the initialization map.",
                3.0));
        scoring.add(new Test(test_01bis,
                "I cannot log-out multiple times using the same transaction, but it should be possible to re-log in, and the novel transaction shall have a different UIID.",
                7.0));
        scoring.add(new Test(test_04,
                "I must neither basket nor purchase unavailable products.",
                7.0));
        scoring.add(new Test(test_05,
                "Logging out automatically re-shelves all the remaining product non-purchased in the basket, and therefore it shall be possible to re-basket the products.",
                3.0));
        scoring.add(new Test(test_05bis,
                "Logging out should also automatically disable all the remaining operations available through the transaction",
                3.0));
        scoring.add(new Test(test_06,
                "Correctly purchasing the available items (single-threaded).",
                20.0));
        scoring.add(new Test(test_11,
                "Correctly shelving the products",
                5.0));
        FunctionScoring(scoring);
        scoring.clear();

        System.out.println("");
        System.out.println("II. Multi-Threaded Correctness");
        System.out.println("==============================");
        System.out.println("");
        scoring.add(new Test(test_03,
                "The same user can log-in multiple times.",
                6.0));
        scoring.add(new Test(test_07,
                "Two threads shall never be able to contemporary access to the same object on the shelf!",
                6.0));
        scoring.add(new Test(test_08,
                "A client running without a supplier shall always dispose the available resources",
                5.0));
        scoring.add(new Test(test_09,
                "Client/Supplier interaction",
                6.0));
        scoring.add(new Test(test_10,
                "Client/Client interaction",
                12.0));
        scoring.add(new Test(test_12,
                "Supplier Stopping Condition",
                3.0));
        FunctionScoring(scoring);

        System.out.println("");
        System.out.println("[" + StudentId + "] Total Score: " + total_score + "/" + total_max_score + " = " + (total_score/total_max_score));
    }

    public static double sumUpOk(Collection<Message> msg) {
        if ((msg == null) || msg.isEmpty()) return 0.0;
        else {
            double N = msg.size();
            double OK = 0.0;
            for (var x : msg) if (x.isOK) OK++;
            return OK / N;
        }
    }

    public static class Test {
        public final Function<Boolean,List<Message>> test;
        public final String name;
        public final double max_score;

        public Test(Function<Boolean, List<Message>> test, String name, double max_score) {
            this.test = test;
            this.name = name;
            this.max_score = max_score;
        }
    }

    public static class Message {
        public final boolean isOK;
        public final String message;

        public Message(boolean isOK, String message) {
            this.isOK = isOK;
            this.message = message;
        }

        public String toString() {
            return (this.isOK ? ANSI_GREEN : ANSI_RED) + message + (ANSI_RESET);
        }
    }


}
