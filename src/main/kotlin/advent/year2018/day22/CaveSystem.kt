package advent.year2018.day22

import advent.utils.*
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader

/**
 * --- Day 22: Mode Maze ---
 * This is it, your final stop: the year -483. It's snowing and dark outside; the only light you can see is coming from
 * a small cottage in the distance. You make your way there and knock on the door.
 * A portly man with a large, white beard answers the door and invites you inside. For someone living near the North
 * Pole in -483, he must not get many visitors, but he doesn't act surprised to see you. Instead, he offers you some
 * milk and cookies.
 * After talking for a while, he asks a favor of you. His friend hasn't come back in a few hours, and he's not sure
 * where he is.  Scanning the region briefly, you discover one life signal in a cave system nearby; his friend must
 * have taken shelter there.  The man asks if you can go there to retrieve his friend.
 * The cave is divided into square regions which are either dominantly rocky, narrow, or wet (called its type). Each
 * region occupies exactly one coordinate in X,Y format where X and Y are integers and zero or greater. (Adjacent
 * regions can be the same type.)
 * The scan (your puzzle input) is not very detailed: it only reveals the depth of the cave system and the coordinates
 * of the target. However, it does not reveal the type of each region. The mouth of the cave is at 0,0.
 * The man explains that due to the unusual geology in the area, there is a method to determine any region's type based
 * on its erosion level. The erosion level of a region can be determined from its geologic index. The geologic index
 * can be determined using the first rule that applies from the list below:
 * 
 * The region at 0,0 (the mouth of the cave) has a geologic index of 0.
 * The region at the coordinates of the target has a geologic index of 0.
 * If the region's Y coordinate is 0, the geologic index is its X coordinate times 16807.
 * If the region's X coordinate is 0, the geologic index is its Y coordinate times 48271.
 * Otherwise, the region's geologic index is the result of multiplying the erosion levels of the regions at X-1,Y and
 * X,Y-1.
 * 
 * A region's erosion level is its geologic index plus the cave system's depth, all modulo 20183. Then:
 * 
 * If the erosion level modulo 3 is 0, the region's type is rocky.
 * If the erosion level modulo 3 is 1, the region's type is wet.
 * If the erosion level modulo 3 is 2, the region's type is narrow.
 * 
 * For example, suppose the cave system's depth is 510 and the target's coordinates are 10,10. Using % to represent the
 * modulo operator, the cavern would look as follows:
 * 
 * At 0,0, the geologic index is 0. The erosion level is (0 + 510) % 20183 = 510. The type is 510 % 3 = 0, rocky.
 * At 1,0, because the Y coordinate is 0, the geologic index is 1 * 16807 = 16807. The erosion level is (16807 + 510) %
 * 20183 = 17317. The type is 17317 % 3 = 1, wet. 
 * At 0,1, because the X coordinate is 0, the geologic index is  1 * 48271 = 48271. The erosion level is (48271 + 510)
 * % 20183 = 8415. The type is 8415 % 3 = 0, rocky.
 * At 1,1, neither coordinate is 0 and it is not the coordinate of the target, so the geologic index is the erosion
 * level of 0,1 (8415) times the erosion level of 1,0 (17317), 8415 * 17317 = 145722555. The erosion level is
 * (145722555 + 510) % 20183 = 1805. The type is 1805 % 3 = 2, narrow.
 * At 10,10, because they are the target's coordinates, the geologic index is 0. The erosion level is (0 + 510) % 20183
 * = 510. The type is 510 % 3 = 0, rocky.
 * 
 * Drawing this same cave system with rocky as ., wet as =, narrow as |, the mouth as M, the target as T, with 0,0 in
 * the top-left corner, X increasing to the right, and Y increasing downward, the top-left corner of the map looks like
 * this:
 * M=.|=.|.|=.|=|=.
 * .|=|=|||..|.=...
 * .==|....||=..|==
 * =.|....|.==.|==.
 * =|..==...=.|==..
 * =||.=.=||=|=..|=
 * |.=.===|||..=..|
 * |..==||=.|==|===
 * .=..===..=|.|||.
 * .======|||=|=.|=
 * .===|=|===T===||
 * =|||...|==..|=.|
 * =.=|=.=..=.||==|
 * ||=|=...|==.=|==
 * |=.=||===.|||===
 * ||.|==.|.|.||=||
 * 
 * Before you go in, you should determine the risk level of the area. For the rectangle that has a top-left corner of
 * region 0,0 and a bottom-right corner of the region containing the target, add up the risk level of each individual
 * region: 0 for rocky regions, 1 for wet regions, and 2 for narrow regions.
 * In the cave system above, because the mouth is at 0,0 and the target is at 10,10, adding up the risk level of all
 * regions with an X coordinate from 0 to 10 and a Y coordinate from 0 to 10, this total is 114.
 * What is the total risk level for the smallest rectangle that includes 0,0 and the target's coordinates?
 * 
 * --- Part Two ---
 * Okay, it's time to go rescue the man's friend.
 * As you leave, he hands you some tools: a torch and some climbing gear. You can't equip both tools at once, but you
 * can choose to use neither.
 * Tools can only be used in certain regions:
 * 
 * In rocky regions, you can use the climbing gear or the torch. You cannot use neither (you'll likely slip and fall).
 * In wet regions, you can use the climbing gear or neither tool. You cannot use the torch (if it gets wet, you won't
 * have a light source).
 * In narrow regions, you can use the torch or neither tool. You cannot use the climbing gear (it's too bulky to fit).
 * 
 * You start at 0,0 (the mouth of the cave) with the torch equipped and must reach the target coordinates as quickly as
 * possible. The regions with negative X or Y are solid rock and cannot be traversed. The fastest route might involve
 * entering regions beyond the X or Y coordinate of the target.
 * You can move to an adjacent region (up, down, left, or right; never diagonally) if your currently equipped tool
 * allows you to enter that region. Moving to an adjacent region takes one minute. (For example, if you have the torch
 * equipped, you can move between rocky and narrow regions, but cannot enter wet regions.)
 * You can change your currently equipped tool or put both away if your new equipment would be valid for your current
 * region. Switching to using the climbing gear, torch, or neither always takes seven minutes, regardless of which
 * tools you start with. (For example, if you are in a rocky region, you can switch from the torch to the climbing
 * gear, but you cannot switch to neither.)
 * Finally, once you reach the target, you need the torch equipped before you can find him in the dark. The target is
 * always in a rocky region, so if you arrive there with climbing gear equipped, you will need to spend seven minutes
 * switching to your torch.
 * For example, using the same cave system as above, starting in the top left corner (0,0) and moving to the bottom
 * right corner (the target, 10,10) as quickly as possible, one possible route is as follows, with your current
 * position marked X:
 * Initially:
 * X=.|=.|.|=.|=|=.
 * .|=|=|||..|.=...
 * .==|....||=..|==
 * =.|....|.==.|==.
 * =|..==...=.|==..
 * =||.=.=||=|=..|=
 * |.=.===|||..=..|
 * |..==||=.|==|===
 * .=..===..=|.|||.
 * .======|||=|=.|=
 * .===|=|===T===||
 * =|||...|==..|=.|
 * =.=|=.=..=.||==|
 * ||=|=...|==.=|==
 * |=.=||===.|||===
 * ||.|==.|.|.||=||
 * 
 * Down:
 * M=.|=.|.|=.|=|=.
 * X|=|=|||..|.=...
 * .==|....||=..|==
 * =.|....|.==.|==.
 * =|..==...=.|==..
 * =||.=.=||=|=..|=
 * |.=.===|||..=..|
 * |..==||=.|==|===
 * .=..===..=|.|||.
 * .======|||=|=.|=
 * .===|=|===T===||
 * =|||...|==..|=.|
 * =.=|=.=..=.||==|
 * ||=|=...|==.=|==
 * |=.=||===.|||===
 * ||.|==.|.|.||=||
 * 
 * Right:
 * M=.|=.|.|=.|=|=.
 * .X=|=|||..|.=...
 * .==|....||=..|==
 * =.|....|.==.|==.
 * =|..==...=.|==..
 * =||.=.=||=|=..|=
 * |.=.===|||..=..|
 * |..==||=.|==|===
 * .=..===..=|.|||.
 * .======|||=|=.|=
 * .===|=|===T===||
 * =|||...|==..|=.|
 * =.=|=.=..=.||==|
 * ||=|=...|==.=|==
 * |=.=||===.|||===
 * ||.|==.|.|.||=||
 * 
 * Switch from using the torch to neither tool:
 * M=.|=.|.|=.|=|=.
 * .X=|=|||..|.=...
 * .==|....||=..|==
 * =.|....|.==.|==.
 * =|..==...=.|==..
 * =||.=.=||=|=..|=
 * |.=.===|||..=..|
 * |..==||=.|==|===
 * .=..===..=|.|||.
 * .======|||=|=.|=
 * .===|=|===T===||
 * =|||...|==..|=.|
 * =.=|=.=..=.||==|
 * ||=|=...|==.=|==
 * |=.=||===.|||===
 * ||.|==.|.|.||=||
 * 
 * Right 3:
 * M=.|=.|.|=.|=|=.
 * .|=|X|||..|.=...
 * .==|....||=..|==
 * =.|....|.==.|==.
 * =|..==...=.|==..
 * =||.=.=||=|=..|=
 * |.=.===|||..=..|
 * |..==||=.|==|===
 * .=..===..=|.|||.
 * .======|||=|=.|=
 * .===|=|===T===||
 * =|||...|==..|=.|
 * =.=|=.=..=.||==|
 * ||=|=...|==.=|==
 * |=.=||===.|||===
 * ||.|==.|.|.||=||
 * 
 * Switch from using neither tool to the climbing gear:
 * M=.|=.|.|=.|=|=.
 * .|=|X|||..|.=...
 * .==|....||=..|==
 * =.|....|.==.|==.
 * =|..==...=.|==..
 * =||.=.=||=|=..|=
 * |.=.===|||..=..|
 * |..==||=.|==|===
 * .=..===..=|.|||.
 * .======|||=|=.|=
 * .===|=|===T===||
 * =|||...|==..|=.|
 * =.=|=.=..=.||==|
 * ||=|=...|==.=|==
 * |=.=||===.|||===
 * ||.|==.|.|.||=||
 * 
 * Down 7:
 * M=.|=.|.|=.|=|=.
 * .|=|=|||..|.=...
 * .==|....||=..|==
 * =.|....|.==.|==.
 * =|..==...=.|==..
 * =||.=.=||=|=..|=
 * |.=.===|||..=..|
 * |..==||=.|==|===
 * .=..X==..=|.|||.
 * .======|||=|=.|=
 * .===|=|===T===||
 * =|||...|==..|=.|
 * =.=|=.=..=.||==|
 * ||=|=...|==.=|==
 * |=.=||===.|||===
 * ||.|==.|.|.||=||
 * 
 * Right:
 * M=.|=.|.|=.|=|=.
 * .|=|=|||..|.=...
 * .==|....||=..|==
 * =.|....|.==.|==.
 * =|..==...=.|==..
 * =||.=.=||=|=..|=
 * |.=.===|||..=..|
 * |..==||=.|==|===
 * .=..=X=..=|.|||.
 * .======|||=|=.|=
 * .===|=|===T===||
 * =|||...|==..|=.|
 * =.=|=.=..=.||==|
 * ||=|=...|==.=|==
 * |=.=||===.|||===
 * ||.|==.|.|.||=||
 * 
 * Down 3:
 * M=.|=.|.|=.|=|=.
 * .|=|=|||..|.=...
 * .==|....||=..|==
 * =.|....|.==.|==.
 * =|..==...=.|==..
 * =||.=.=||=|=..|=
 * |.=.===|||..=..|
 * |..==||=.|==|===
 * .=..===..=|.|||.
 * .======|||=|=.|=
 * .===|=|===T===||
 * =|||.X.|==..|=.|
 * =.=|=.=..=.||==|
 * ||=|=...|==.=|==
 * |=.=||===.|||===
 * ||.|==.|.|.||=||
 * 
 * Right:
 * M=.|=.|.|=.|=|=.
 * .|=|=|||..|.=...
 * .==|....||=..|==
 * =.|....|.==.|==.
 * =|..==...=.|==..
 * =||.=.=||=|=..|=
 * |.=.===|||..=..|
 * |..==||=.|==|===
 * .=..===..=|.|||.
 * .======|||=|=.|=
 * .===|=|===T===||
 * =|||..X|==..|=.|
 * =.=|=.=..=.||==|
 * ||=|=...|==.=|==
 * |=.=||===.|||===
 * ||.|==.|.|.||=||
 * 
 * Down:
 * M=.|=.|.|=.|=|=.
 * .|=|=|||..|.=...
 * .==|....||=..|==
 * =.|....|.==.|==.
 * =|..==...=.|==..
 * =||.=.=||=|=..|=
 * |.=.===|||..=..|
 * |..==||=.|==|===
 * .=..===..=|.|||.
 * .======|||=|=.|=
 * .===|=|===T===||
 * =|||...|==..|=.|
 * =.=|=.X..=.||==|
 * ||=|=...|==.=|==
 * |=.=||===.|||===
 * ||.|==.|.|.||=||
 * 
 * Right 4:
 * M=.|=.|.|=.|=|=.
 * .|=|=|||..|.=...
 * .==|....||=..|==
 * =.|....|.==.|==.
 * =|..==...=.|==..
 * =||.=.=||=|=..|=
 * |.=.===|||..=..|
 * |..==||=.|==|===
 * .=..===..=|.|||.
 * .======|||=|=.|=
 * .===|=|===T===||
 * =|||...|==..|=.|
 * =.=|=.=..=X||==|
 * ||=|=...|==.=|==
 * |=.=||===.|||===
 * ||.|==.|.|.||=||
 * 
 * Up 2:
 * M=.|=.|.|=.|=|=.
 * .|=|=|||..|.=...
 * .==|....||=..|==
 * =.|....|.==.|==.
 * =|..==...=.|==..
 * =||.=.=||=|=..|=
 * |.=.===|||..=..|
 * |..==||=.|==|===
 * .=..===..=|.|||.
 * .======|||=|=.|=
 * .===|=|===X===||
 * =|||...|==..|=.|
 * =.=|=.=..=.||==|
 * ||=|=...|==.=|==
 * |=.=||===.|||===
 * ||.|==.|.|.||=||
 * 
 * Switch from using the climbing gear to the torch:
 * M=.|=.|.|=.|=|=.
 * .|=|=|||..|.=...
 * .==|....||=..|==
 * =.|....|.==.|==.
 * =|..==...=.|==..
 * =||.=.=||=|=..|=
 * |.=.===|||..=..|
 * |..==||=.|==|===
 * .=..===..=|.|||.
 * .======|||=|=.|=
 * .===|=|===X===||
 * =|||...|==..|=.|
 * =.=|=.=..=.||==|
 * ||=|=...|==.=|==
 * |=.=||===.|||===
 * ||.|==.|.|.||=||
 * 
 * This is tied with other routes as the fastest way to reach the target: 45 minutes. In it, 21 minutes are spent
 * switching tools (three times, seven minutes each) and the remaining 24 minutes are spent moving.
 * What is the fewest number of minutes you can take to reach the target?
 * 
 */
class CaveSystem(val depth: Int,
                 val target: Point) {

    private val geologicalIndices = CacheBuilder.newBuilder()
            .build(CacheLoader.from { point: Point? -> calculateGeologicalIndex(point!!) })

    fun geologicalIndex(point: Point): Int = geologicalIndices.get(point)

    private fun calculateGeologicalIndex(point: Point): Int = when {
        point == Point(0, 0) -> 0
        point == target -> 0
        point.y == 0 -> point.x * 16807
        point.x == 0 -> point.y * 48271
        else -> erosionLevel(point + Point(-1, 0)) *
                erosionLevel(point + Point(0, -1))
    }

    fun erosionLevel(point: Point) = (geologicalIndex(point) + depth) % 20183

    fun regionType(point: Point) = when (val mod = erosionLevel(point) % 3) {
        0 -> RegionType.ROCKY
        1 -> RegionType.WET
        2 -> RegionType.NARROW
        else -> throw IllegalStateException("This should not be possible, region type $mod")
    }

    private fun rectangle(upperLeft: Point, lowerRight: Point): List<List<Point>> =
            (upperLeft.y..lowerRight.y).map { y ->
                (upperLeft.x..lowerRight.x).map { x -> Point(x, y) }
            }

    private fun charAt(point: Point) = when (point) {
        Point(0, 0) -> 'M'
        target -> 'T'
        else -> regionType(point).char
    }

    fun map(lowerRight: Point): String = rectangle(Point(0, 0), lowerRight).joinToString("\n") { row ->
        row.joinToString("") { point -> Character.toString(charAt(point)) }
    }

    fun totalRisk() = rectangle(Point(0, 0), target).sumBy { row -> row.sumBy { regionType(it).riskLevel } }

    fun rescueTime(): Int {
        val finder = ShortestPathFinder()

        val shortestPaths = finder.find(start = RescuerState(Point(0, 0), CaveTool.TORCH),
                end = EndState(RescuerState(target, CaveTool.TORCH)),
                filter = Filter {
                    val last = it.last()
                    // This is kind of a hack - we don't expect to have to go too far beyond the bounds of where
                    // our target's X and Y are so we will prune those branches aggressively. If this is wrong, we
                    // might end up with an answer that is too high. Without this aggressive pruning, the path finding
                    // takes uncomfortably long.
                    last.position.x > 2 * target.x || last.position.y > 2 * target.y
                },
                // We don't care about any 2 paths that share the same current state - we can just pick one of those
                // arbitrarily.
                collapse = Collapse { steps: List<RescuerState> -> steps.last() },
                nextSteps = StepsWithCost { it.nextSteps(this) })

        return shortestPaths.first().totalCost
    }
}

enum class RegionType(val riskLevel: Int,
                      val char: Char,
                      val legalTools: Set<CaveTool>) {
    ROCKY(riskLevel = 0, char = '.', legalTools = setOf(CaveTool.CLIMBING_GEAR, CaveTool.TORCH)),
    WET(riskLevel = 1, char = '=', legalTools = setOf(CaveTool.CLIMBING_GEAR, CaveTool.NEITHER)),
    NARROW(riskLevel = 2, char = '|', legalTools = setOf(CaveTool.TORCH, CaveTool.NEITHER))
}

enum class CaveTool { CLIMBING_GEAR, TORCH, NEITHER }

private data class RescuerState(val position: Point, val tool: CaveTool) {
    fun nextSteps(cave: CaveSystem): Set<Step<RescuerState>> {
        val toolChanges = CaveTool.values()
                .filter { it != tool && cave.regionType(position).legalTools.contains(it) }
                .map { Step(next = RescuerState(position, it), cost = 7) }
                .toSet()

        val moves = position.adjacentNeighbors
                .filter { it.x >= 0 && it.y >= 0 }
                .filter { cave.regionType(it).legalTools.contains(tool) }
                .map { Step(next = RescuerState(it, tool), cost = 1) }

        return toolChanges + moves
    }
}

fun main() {
    val cave = CaveSystem(depth = 5355, target = Point(14, 796))

    println(cave.totalRisk())
    println(cave.rescueTime())
}