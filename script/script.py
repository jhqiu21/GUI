import random, textwrap, pathlib, os
from collections import Counter

ROWS, COLS  = 4, 4
OUT_DIR     = "generated_progs"
PY_CMDS     = ["LOAD 0", "ADD", "NOP"]

INPUT_SOURCES = [
    "Open", "north_in", "south_in", "west_in", "east_in", "ALUOut"
]

DEST_PORTS = [
    "predicate", "south_out", "west_out",
    "north_out", "east_out", "alu_op2", "alu_op1"
]

def generate_switch_block(row, col, rows, cols) -> str:
    usage = Counter()
    lines = []

    forbidden = set()
    if row == 0:
        forbidden.add("north_out")
    if row == rows-1:
        forbidden.add("south_out")
    if col == 0:
        forbidden.add("west_out")
    if col == cols-1:
        forbidden.add("east_out")

    for dest in DEST_PORTS:
        if dest in forbidden:
            src = "Open"
            lines.append(f"    {src} -> {dest},")
            continue

        candidates = [s for s in INPUT_SOURCES if usage[s] < 2]
        src = random.choice(candidates)
        usage[src] += 1
        lines.append(f"    {src} -> {dest},")

    random.shuffle(lines)
    return "switch_config: {\n" + "\n".join(lines) + "\n};"

def random_inst_sequence():
    n = random.randint(4, 8)
    has_jump = random.choice([True, False])
    jump_pos = random.randint(0, n-2) if has_jump else -1

    seq = []
    for i in range(n):
        if i == jump_pos:
            x = random.randint(0, n-2)
            seq.append(f"JUMP [{x}, {n-1}]")
        else:
            seq.append(random.choice(PY_CMDS))
    return seq

def generate_prog(row, col, rows, cols):
    insts = random_inst_sequence()
    parts = []

    for op in insts:
        parts.append(f"operation: {op}")
        parts.append(generate_switch_block(row, col, rows, cols))
        parts.append("input_register_used: {};")
        parts.append("input_register_write: {};")
        parts.append("")
    return "\n".join(parts)

def main():
    out_path = pathlib.Path(OUT_DIR)
    if out_path.exists():
        for p in out_path.iterdir():
            p.unlink()
    else:
        out_path.mkdir(parents=True)

    for r in range(ROWS):
        for c in range(COLS):
            content = generate_prog(r, c, ROWS, COLS)
            (out_path / f"PE-Y{r}X{c}.prog").write_text(content)

    print(f"Generated {ROWS*COLS} .prog files in '{OUT_DIR}'")

if __name__ == "__main__":
    main()
