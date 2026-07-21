package io.github.hello09x.fakeplayer.core.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.hello09x.fakeplayer.api.spi.ActionSetting;
import io.github.hello09x.fakeplayer.api.spi.ActionType;
import io.github.hello09x.fakeplayer.core.command.impl.*;
import io.github.hello09x.fakeplayer.core.constant.Direction;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Singleton
public class PlayerCommand {

    public final static String PERMISSION = "fakeplayer.command";

    private final static SimpleCommandExceptionType NOT_FOUND =
            new SimpleCommandExceptionType(() -> "No such fake player");

    private final FakeplayerManager manager;
    private final ActionCommand actionCommand;
    private final SpawnCommand spawnCommand;
    private final KillCommand killCommand;
    private final KillallCommand killallCommand;
    private final ListCommand listCommand;
    private final StatusCommand statusCommand;
    private final TeleportCommand teleportCommand;
    private final MoveCommand moveCommand;
    private final SwapCommand swapCommand;
    private final HoldCommand holdCommand;
    private final RideCommand rideCommand;
    private final RotationCommand rotationCommand;
    private final ReloadCommand reloadCommand;
    private final DebugCommand debugCommand;

    @Inject
    public PlayerCommand(
            FakeplayerManager manager,
            ActionCommand actionCommand,
            SpawnCommand spawnCommand,
            KillCommand killCommand,
            KillallCommand killallCommand,
            ListCommand listCommand,
            StatusCommand statusCommand,
            TeleportCommand teleportCommand,
            MoveCommand moveCommand,
            SwapCommand swapCommand,
            HoldCommand holdCommand,
            RideCommand rideCommand,
            RotationCommand rotationCommand,
            ReloadCommand reloadCommand,
            DebugCommand debugCommand
    ) {
        this.manager = manager;
        this.actionCommand = actionCommand;
        this.spawnCommand = spawnCommand;
        this.killCommand = killCommand;
        this.killallCommand = killallCommand;
        this.listCommand = listCommand;
        this.statusCommand = statusCommand;
        this.teleportCommand = teleportCommand;
        this.moveCommand = moveCommand;
        this.swapCommand = swapCommand;
        this.holdCommand = holdCommand;
        this.rideCommand = rideCommand;
        this.rotationCommand = rotationCommand;
        this.reloadCommand = reloadCommand;
        this.debugCommand = debugCommand;
    }

    public void register(@NotNull JavaPlugin plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            var root = Commands.literal("player")
                    .requires(src -> src.getSender().hasPermission(PERMISSION));

            root.then(Commands.literal("list").executes(ctx -> {
                listCommand.list(ctx.getSource().getSender());
                return Command.SINGLE_SUCCESS;
            }));
            root.then(Commands.literal("killall").executes(ctx -> {
                killallCommand.killall(ctx.getSource().getSender());
                return Command.SINGLE_SUCCESS;
            }));
            root.then(Commands.literal("reload").executes(ctx -> {
                reloadCommand.reload(ctx.getSource().getSender());
                return Command.SINGLE_SUCCESS;
            }));
            root.then(buildDebug());

            var nameArg = Commands.argument("name", StringArgumentType.word())
                    .suggests(this::suggestNames);
            addNameActions(nameArg);
            root.then(nameArg);

            event.registrar().register(root.build(), "Carpet-style fake player control");
        });
    }


    private LiteralArgumentBuilder<CommandSourceStack> literal(String name) {
        return Commands.literal(name);
    }

    private void addNameActions(ArgumentBuilder<CommandSourceStack, ?> b) {
        b.then(literal("spawn")
                .executes(ctx -> { doSpawn(ctx, null, null); return Command.SINGLE_SUCCESS; })
                .then(literal("at").then(dbl("x").then(dbl("y").then(dbl("z")
                        .executes(ctx -> { doSpawn(ctx, readPos(ctx), null); return Command.SINGLE_SUCCESS; })
                        .then(literal("facing").then(flt("yaw").then(flt("pitch")
                                .executes(ctx -> { doSpawn(ctx, readPos(ctx), readRot(ctx)); return Command.SINGLE_SUCCESS; })))))))));

        b.then(action("use", ActionType.USE));
        b.then(action("jump", ActionType.JUMP));
        b.then(action("attack", ActionType.ATTACK));
        b.then(action("drop", ActionType.DROP_ITEM));
        b.then(action("dropstack", ActionType.DROP_STACK));

        b.then(literal("stop").executes(withFake((s, f) -> actionCommand.stop(s, f))));
        b.then(literal("kill").executes(withFake((s, f) -> killCommand.kill(s, f))));
        b.then(literal("status").executes(withFake((s, f) -> statusCommand.status(s, f))));
        b.then(literal("swaphands").executes(withFake((s, f) -> swapCommand.swap(f))));

        b.then(literal("sneak").executes(withFake((s, f) -> f.setSneaking(true))));
        b.then(literal("unsneak").executes(withFake((s, f) -> f.setSneaking(false))));
        b.then(literal("sprint").executes(withFake((s, f) -> f.setSprinting(true))));
        b.then(literal("unsprint").executes(withFake((s, f) -> f.setSprinting(false))));

        b.then(literal("mount").executes(withFake((s, f) -> rideCommand.mount(s, f))));
        b.then(literal("dismount").executes(withFake((s, f) -> rideCommand.dismount(f))));

        b.then(literal("tpswap").executes(ctx -> {
            var sender = ctx.getSource().getSender();
            if (!(sender instanceof Player p)) {
                sender.sendMessage(Component.text("Only a player can use tpswap"));
                return Command.SINGLE_SUCCESS;
            }
            teleportCommand.tpswap(p, resolve(ctx));
            return Command.SINGLE_SUCCESS;
        }));

        b.then(literal("hotbar").then(Commands.argument("slot", IntegerArgumentType.integer(1, 9))
                .executes(ctx -> { holdCommand.hold(resolve(ctx), IntegerArgumentType.getInteger(ctx, "slot")); return Command.SINGLE_SUCCESS; })));

        b.then(buildLook());
        b.then(buildTurn());
        b.then(buildMove());
    }

    private LiteralArgumentBuilder<CommandSourceStack> action(String name, ActionType type) {
        return literal(name)
                .executes(actionExec(type, ActionSetting.once()))
                .then(literal("once").executes(actionExec(type, ActionSetting.once())))
                .then(literal("continuous").executes(actionExec(type, ActionSetting.continuous())))
                .then(literal("interval").then(Commands.argument("ticks", IntegerArgumentType.integer(1))
                        .executes(ctx -> {
                            actionCommand.dispatch(ctx.getSource().getSender(), resolve(ctx), type,
                                    ActionSetting.interval(IntegerArgumentType.getInteger(ctx, "ticks")));
                            return Command.SINGLE_SUCCESS;
                        })));
    }

    private com.mojang.brigadier.Command<CommandSourceStack> actionExec(ActionType type, ActionSetting setting) {
        return ctx -> {
            actionCommand.dispatch(ctx.getSource().getSender(), resolve(ctx), type, setting.clone());
            return Command.SINGLE_SUCCESS;
        };
    }

    private LiteralArgumentBuilder<CommandSourceStack> buildLook() {
        var look = literal("look");
        for (var dir : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN}) {
            look.then(literal(dir.name().toLowerCase()).executes(withFake((s, f) -> rotationCommand.lookDirection(f, dir))));
        }
        look.then(literal("at").then(dbl("x").then(dbl("y").then(dbl("z")
                .executes(ctx -> { var p = readPos(ctx); rotationCommand.lookAt(resolve(ctx), p.getX(), p.getY(), p.getZ()); return Command.SINGLE_SUCCESS; })))));
        look.then(flt("yaw").then(flt("pitch").executes(ctx -> {
            rotationCommand.lookRotation(resolve(ctx), readYaw(ctx), readPitch(ctx));
            return Command.SINGLE_SUCCESS;
        })));
        return look;
    }

    private LiteralArgumentBuilder<CommandSourceStack> buildTurn() {
        var turn = literal("turn");
        turn.then(literal("left").executes(withFake((s, f) -> rotationCommand.turn(f, -90, 0))));
        turn.then(literal("right").executes(withFake((s, f) -> rotationCommand.turn(f, 90, 0))));
        turn.then(literal("back").executes(withFake((s, f) -> rotationCommand.turn(f, 180, 0))));

        turn.then(flt("yaw").then(flt("pitch").executes(ctx -> {
            rotationCommand.turn(resolve(ctx), readYaw(ctx), readPitch(ctx));
            return Command.SINGLE_SUCCESS;
        })));
        return turn;
    }

    private LiteralArgumentBuilder<CommandSourceStack> buildMove() {
        var move = literal("move");
        move.then(literal("forward").executes(withFake((s, f) -> moveCommand.move(f, 1, 0))));
        move.then(literal("backward").executes(withFake((s, f) -> moveCommand.move(f, -1, 0))));
        move.then(literal("left").executes(withFake((s, f) -> moveCommand.move(f, 0, 1))));
        move.then(literal("right").executes(withFake((s, f) -> moveCommand.move(f, 0, -1))));
        return move;
    }

    private LiteralArgumentBuilder<CommandSourceStack> buildDebug() {
        return literal("debug").then(Commands.argument("channel", StringArgumentType.word())
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            debugCommand.sendPluginMessage(ctx.getSource().getSender(),
                                    StringArgumentType.getString(ctx, "channel"),
                                    StringArgumentType.getString(ctx, "message"));
                            return Command.SINGLE_SUCCESS;
                        })));
    }

// helpers

    private com.mojang.brigadier.Command<CommandSourceStack> withFake(FakeAction action) {
        return ctx -> {
            action.run(ctx.getSource().getSender(), resolve(ctx));
            return Command.SINGLE_SUCCESS;
        };
    }

    private @NotNull Player resolve(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var sender = ctx.getSource().getSender();
        var name = StringArgumentType.getString(ctx, "name");
        var fake = sender.isOp() ? manager.get(name) : manager.get(sender, name);
        if (fake == null) {
            throw NOT_FOUND.create();
        }
        return fake;
    }

    private void doSpawn(CommandContext<CommandSourceStack> ctx, Location at, float[] facing) {
        var sender = ctx.getSource().getSender();
        var name = StringArgumentType.getString(ctx, "name");
        Location where = at;
        if (where == null) {
            where = sender instanceof Player p ? p.getLocation().clone() : null;
        }
        spawnCommand.spawn(sender, name, where, facing);
    }

    private CompletableFuture<Suggestions> suggestNames(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder b) {
        var sender = ctx.getSource().getSender();
        var fakes = sender.isOp() ? manager.getAll() : manager.getAll(sender);
        var token = b.getRemaining().toLowerCase();
        for (var fake : fakes) {
            if (fake.getName().toLowerCase().contains(token)) {
                b.suggest(fake.getName());
            }
        }
        return b.buildFuture();
    }

    private com.mojang.brigadier.builder.RequiredArgumentBuilder<CommandSourceStack, Double> dbl(String name) {
        return Commands.argument(name, com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg());
    }

    private com.mojang.brigadier.builder.RequiredArgumentBuilder<CommandSourceStack, Float> flt(String name) {
        return Commands.argument(name, FloatArgumentType.floatArg());
    }

    private Location readPos(CommandContext<CommandSourceStack> ctx) {
        var sender = ctx.getSource().getSender();
        var world = sender instanceof Player p ? p.getWorld() : org.bukkit.Bukkit.getWorlds().get(0);
        return new Location(world,
                com.mojang.brigadier.arguments.DoubleArgumentType.getDouble(ctx, "x"),
                com.mojang.brigadier.arguments.DoubleArgumentType.getDouble(ctx, "y"),
                com.mojang.brigadier.arguments.DoubleArgumentType.getDouble(ctx, "z"));
    }

    private float[] readRot(CommandContext<CommandSourceStack> ctx) {
        return new float[]{readYaw(ctx), readPitch(ctx)};
    }

    private float readYaw(CommandContext<CommandSourceStack> ctx) {
        return FloatArgumentType.getFloat(ctx, "yaw");
    }

    private float readPitch(CommandContext<CommandSourceStack> ctx) {
        return FloatArgumentType.getFloat(ctx, "pitch");
    }

    @FunctionalInterface
    private interface FakeAction {
        void run(@NotNull CommandSender sender, @NotNull Player fake) throws CommandSyntaxException;
    }
}
