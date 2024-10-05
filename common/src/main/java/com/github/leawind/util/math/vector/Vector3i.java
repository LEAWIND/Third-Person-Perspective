package com.github.leawind.util.math.vector;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Vector3i {
	private int x;
	private int y;
	private int z;

	public Vector3i (int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Contract(value=" -> new", pure=true)
	public static @NotNull Vector3i of () {
		return of(0);
	}

	@Contract(value="_ -> new", pure=true)
	public static @NotNull Vector3i of (int d) {
		return of(d, d, d);
	}

	@Contract(value="_,_,_ -> new", pure=true)
	public static @NotNull Vector3i of (int x, int y, int z) {
		return new Vector3i(x, y, z);
	}

	@Override
	public int hashCode () {
		return (y + z * 31) * 31 + x;
	}

	@Override
	public boolean equals (Object other) {
		if (this == other) {
			return true;
		} else if (other instanceof Vector3i vec) {
			if (x != vec.x) {
				return false;
			} else if (y != vec.y) {
				return false;
			} else {
				return z == vec.z;
			}
		} else {
			return false;
		}
	}

	@Override
	public String toString () {
		return String.format("Vector3i(%d, %d, %d)", x, y, z);
	}

	public int x () {
		return x;
	}

	public int y () {
		return y;
	}

	public int z () {
		return z;
	}

	public void x (int x) {
		this.x = x;
	}

	public void y (int y) {
		this.y = y;
	}

	public void z (int z) {
		this.z = z;
	}

	@Contract("_ -> this")
	public Vector3i set (int d) {
		return set(d, d, d);
	}

	@Contract("_,_,_ -> this")
	public Vector3i set (int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	@Contract("_ -> this")
	public Vector3i set (@NotNull Vector3i v) {
		return set(v.x, v.y, v.z);
	}

	@Contract("_,_ -> param2")
	public Vector3i add (@NotNull Vector3i v, @NotNull Vector3i dest) {
		return add(v.x, v.y, v.z, dest);
	}

	@Contract("_,_,_,_ -> param4")
	public Vector3i add (int x, int y, int z, @NotNull Vector3i dest) {
		dest.x(this.x + x);
		dest.y(this.y + y);
		dest.z(this.z + z);
		return dest;
	}

	@Contract("_ -> this")
	public Vector3i add (@NotNull Vector3i v) {
		return add(v.x, v.y, v.z);
	}

	@Contract("_,_,_ -> this")
	public Vector3i add (int x, int y, int z) {
		this.x = this.x + x;
		this.y = this.y + y;
		this.z = this.z + z;
		return this;
	}

	@Contract("_ -> this")
	public Vector3i add (int d) {
		return add(d, d, d);
	}

	@Contract("_,_ -> param2")
	public Vector3i sub (@NotNull Vector3i v, @NotNull Vector3i dest) {
		return sub(v.x, v.y, v.z, dest);
	}

	@Contract("_,_,_,_ -> param4")
	public Vector3i sub (int x, int y, int z, @NotNull Vector3i dest) {
		dest.x(this.x - x);
		dest.y(this.y - y);
		dest.z(this.z - z);
		return dest;
	}

	@Contract("_ -> this")
	public Vector3i sub (@NotNull Vector3i v) {
		return sub(v.x, v.y, v.z);
	}

	@Contract("_,_,_ -> this")
	public Vector3i sub (int x, int y, int z) {
		this.x = this.x - x;
		this.y = this.y - y;
		this.z = this.z - z;
		return this;
	}

	@Contract("_,_ -> param2")
	public Vector3i mul (@NotNull Vector3i v, @NotNull Vector3i dest) {
		return mul(v.x, v.y, v.z, dest);
	}

	@Contract("_,_,_,_ -> param4")
	public Vector3i mul (int x, int y, int z, @NotNull Vector3i dest) {
		dest.x(this.x * x);
		dest.y(this.y * y);
		dest.z(this.z * z);
		return dest;
	}

	@Contract("_ -> this")
	public Vector3i mul (@NotNull Vector3i v) {
		return mul(v.x, v.y, v.z);
	}

	@Contract("_,_,_ -> this")
	public Vector3i mul (int x, int y, int z) {
		this.x = this.x * x;
		this.y = this.y * y;
		this.z = this.z * z;
		return this;
	}

	@Contract("_ -> this")
	public Vector3i mul (int d) {
		return mul(d, d, d);
	}

	@Contract("_,_ -> param2")
	public Vector3i div (@NotNull Vector3i v, @NotNull Vector3i dest) {
		return div(v.x, v.y, v.z, dest);
	}

	@Contract("_,_,_,_ -> param4")
	public Vector3i div (int x, int y, int z, @NotNull Vector3i dest) {
		dest.x(this.x / x);
		dest.y(this.y / y);
		dest.z(this.z / z);
		return dest;
	}

	@Contract("_ -> this")
	public Vector3i div (@NotNull Vector3i v) {
		return div(v.x, v.y, v.z);
	}

	@Contract("_,_,_ -> this")
	public Vector3i div (int x, int y, int z) {
		this.x = this.x / x;
		this.y = this.y / y;
		this.z = this.z / z;
		return this;
	}

	@Contract("_ -> this")
	public Vector3i div (int d) {
		return div(d, d, d);
	}

	@Contract("_,_ -> param2")
	public Vector3i pow (@NotNull Vector3i v, @NotNull Vector3i dest) {
		return pow(v.x, v.y, v.z, dest);
	}

	@Contract("_,_,_,_ -> param4")
	public Vector3i pow (int x, int y, int z, @NotNull Vector3i dest) {
		dest.x((int)Math.pow(this.x, x));
		dest.y((int)Math.pow(this.y, y));
		dest.z((int)Math.pow(this.z, z));
		return dest;
	}

	@Contract("_,_ -> param2")
	public Vector3i pow (int d, @NotNull Vector3i dest) {
		return pow(d, d, d, dest);
	}

	@Contract("_ -> this")
	public Vector3i pow (@NotNull Vector3i v) {
		return pow(v.x, v.y, v.z);
	}

	@Contract("_,_,_ -> this")
	public Vector3i pow (int x, int y, int z) {
		this.x = (int)Math.pow(this.x, x);
		this.y = (int)Math.pow(this.y, y);
		this.z = (int)Math.pow(this.z, z);
		return this;
	}

	@Contract("_ -> this")
	public Vector3i pow (int d) {
		return pow(d, d, d);
	}

	public int lengthSquared () {
		return x * x + y * y + z * z;
	}

	public double distance (@NotNull Vector3i v) {
		return distance(v.x, v.y, v.z);
	}

	public double distance (int x, int y, int z) {
		int dx = this.x - x;
		int dy = this.y - y;
		int dz = this.z - z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	public int distanceSquared (int x, int y, int z) {
		int dx = this.x - x;
		int dy = this.y - y;
		int dz = this.z - z;
		return dx * dx + dy * dy + dz * dz;
	}

	public double length () {
		return Math.sqrt(x * x + y * y + z * z);
	}

	@Contract("-> this")
	public Vector3i normalize () {
		double len = length();
		x = (int)(x / len);
		y = (int)(y / len);
		z = (int)(z / len);
		return this;
	}

	@Contract("-> this")
	public Vector3i normalizeSafely () {
		double len = length();
		if (len == 0) {
			throw new IllegalStateException("Vector length is 0");
		}
		x = (int)(x / len);
		y = (int)(y / len);
		z = (int)(z / len);
		return this;
	}

	@Contract("_ -> this")
	public Vector3i normalizeSafely (double length) {
		double len = length() / length;
		if (len == 0) {
			throw new IllegalStateException("Vector length is 0");
		}
		x = (int)(x / len);
		y = (int)(y / len);
		z = (int)(z / len);
		return this;
	}

	@Contract("_ -> this")
	public Vector3i rotateTo (@NotNull Vector3i direction) {
		return direction.normalize(length());
	}

	@Contract("_ -> this")
	public Vector3i normalize (double length) {
		length /= length();
		x = (int)(x / length);
		y = (int)(y / length);
		z = (int)(z / length);
		return this;
	}

	@Contract("-> this")
	public Vector3i zero () {
		x = y = z = 0;
		return this;
	}

	@Contract("-> this")
	public Vector3i negate () {
		x = -x;
		y = -y;
		z = -z;
		return this;
	}

	public int dot (@NotNull Vector3i v) {
		return x * v.x + y * v.y + z * v.z;
	}

	@Contract("_,_ -> this")
	public Vector3i clamp (int min, int max) {
		x = Math.min(Math.max(x, min), max);
		y = Math.min(Math.max(y, min), max);
		z = Math.min(Math.max(z, min), max);
		return this;
	}

	@Contract("_,_ -> this")
	public Vector3i clamp (@NotNull Vector3i min, @NotNull Vector3i max) {
		x = Math.min(Math.max(x, min.x), max.x);
		y = Math.min(Math.max(y, min.y), max.y);
		z = Math.min(Math.max(z, min.z), max.z);
		return this;
	}

	@Contract("_,_ -> this")
	public Vector3i lerp (@NotNull Vector3i end, int t) {
		x += (end.x - x) * t;
		y += (end.y - y) * t;
		z += (end.z - z) * t;
		return this;
	}

	@Contract("_,_ -> this")
	public Vector3i lerp (@NotNull Vector3i end, @NotNull Vector3i t) {
		x += (end.x - x) * t.x;
		y += (end.y - y) * t.y;
		z += (end.z - z) * t.z;
		return this;
	}

	@Contract("-> this")
	public Vector3i absolute () {
		x = Math.abs(x);
		y = Math.abs(y);
		z = Math.abs(z);
		return this;
	}

	@Contract("-> new")
	public Vector3i copy () {
		return Vector3i.of(x, y, z);
	}

	public boolean isFinite () {
		return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z);
	}
}
