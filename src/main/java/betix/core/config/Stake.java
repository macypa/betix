package betix.core.config;

public enum Stake {
    noStake(0),
    stake1(1),
    stake2(2),
    stake3(3),
    stake4(4),
    stake5(5),
    stake6(6),
    stake7(7),
    stake8(8),
    stake9(9),
    stake10(10),
    stake11(11),
    stake12(12),
    stake13(13),
    stake14(14),
    stake15(15),
    stake16(16),
    stake17(17),
    stake18(18),
    stake19(19),
    stake20(20);

    private final Configuration stakesConfig = new Configuration(Configuration.STAKES_FILE);
    private boolean useFibonacci = stakesConfig.getConfigAsBoolean(ConfigKey.useFibonacciForStakes);
    private boolean useMartingale = stakesConfig.getConfigAsBoolean(ConfigKey.useMartingaleForStakes);
    private double minBetStake = stakesConfig.getConfigAsDouble(ConfigKey.stake1);
    private int lastStakeCount = stakesConfig.getConfigAsInteger(ConfigKey.lastStakeCount);

    private final int stakeCount;
    public final Double value;

    private Stake(int stakeCount) {
        this.stakeCount = stakeCount;
        this.value = getStake(stakeCount);
    }

    public Stake next() {
        if (lastStakeCount <= stakeCount) {
            return stake1;
        }
        return getAtCount(stakeCount + 1);
    }

    private Double getStake(int stakeCount) {
        if (stakeCount == 0) {
            return 0d;
        }
        if (useFibonacci) {
            return fibonacci(stakeCount);
        } else if (useMartingale) {
            return minBetStake * Math.pow(2, stakeCount - 1);
        } else {
            try {
                return stakesConfig.getConfigAsDouble(ConfigKey.valueOf("stake" + stakeCount));
            } catch (Exception e) {
                return fibonacci(stakeCount);
            }
        }
    }

    private Stake getAtCount(int stakeCount) {
        return get(getStake(stakeCount));
    }

    private double fibonacci(int number) {
        if (number == 1) {
            return minBetStake;
        }
        if (number == 2) {
            return minBetStake * 2;
        }

        return fibonacci(number - 1) + fibonacci(number - 2);
    }

    public static Stake get(double stake) {
        for (Stake s : Stake.values()) {
            if (s.value == stake) {
                return s;
            }
        }
        return noStake;
    }

    public static Stake get(String stake) {
        for (Stake s : Stake.values()) {
            if (s.toString().equals(stake)) {
                return s;
            }
        }
        return noStake;
    }

    @Override
    public String toString() {
        if (value % 1 == 0) {
            return String.valueOf(value.intValue());
        }
        return String.valueOf(value);
    }
}
