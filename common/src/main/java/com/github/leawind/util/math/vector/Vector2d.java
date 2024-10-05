package com.github.leawind.util.math.vector;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Vector2d {
	private double x;
	private double y;

	public Vector2d (double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Contract("-> new")
	public static @NotNull Vector2d of () {
		return of(0);
	}

	@Contract("_ -> new")
	public static @NotNull Vector2d of (double d) {
		return of(d, d);
	}

	@Contract("_,_ -> new")
	public static @NotNull Vector2d of (double x, double y) {
		return new Vector2d(x, y);
	}

	@Contract("_ -> new")
	public static @NotNull Vector2d of (@NotNull Vector2d v) {
		return of(v.x(), v.y());
	}

	@Override
	public int hashCode () {
		int r = 1;
		r = r * 31 + Double.hashCode(x);
		r = r * 31 + Double.hashCode(y);
		return r;
	}

	@Override
	public boolean equals (Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		var other = (Vector2d)obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x())) {
			return false;
		}
		return Double.doubleToLongBits(y) == Double.doubleToLongBits(other.y());
	}

	@Override
	public String toString () {
		return String.format("Vector2d(%f, %f)", x(), y());
	}

	public double x () {
		return x;
	}

	public double y () {
		return y;
	}

	public void x (double x) {
		this.x = x;
	}

	public void y (double y) {
		this.y = y;
	}

	@Contract("_ -> this")
	public Vector2d set (double d) {
		return set(d, d);
	}

	@Contract("_ -> this")
	public Vector2d set (@NotNull Vector2d v) {
		return set(v.x(), v.y());
	}

	@Contract("_,_ -> this")
	public Vector2d set (double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}

	@Contract("_,_ -> param2")
	public Vector2d add (@NotNull Vector2d v, @NotNull Vector2d dest) {
		return add(v.x(), v.y(), dest);
	}

	@Contract("_,_,_ -> param3")
	public Vector2d add (double x, double y, @NotNull Vector2d dest) {
		dest.x(this.x + x);
		dest.y(this.y + y);
		return dest;
	}

	@Contract("_ -> this")
	public Vector2d add (@NotNull Vector2d v) {
		return add(v.x(), v.y());
	}

	@Contract("_ -> this")
	public Vector2d add (double d) {
		return add(d, d);
	}

	@Contract("_,_ -> this")
	public Vector2d add (double x, double y) {
		this.x = this.x + x;
		this.y = this.y + y;
		return this;
	}

	@Contract("_,_ -> param2")
	public Vector2d sub (@NotNull Vector2d v, @NotNull Vector2d dest) {
		return sub(v.x(), v.y(), dest);
	}

	public Vector2d sub (double x, double y, @NotNull Vector2d dest) {
		dest.x(this.x - x);
		dest.y(this.y - y);
		return dest;
	}

	@Contract("_ -> this")
	public Vector2d sub (@NotNull Vector2d v) {
		return sub(v.x(), v.y());
	}

	@Contract("_,_ -> this")
	public Vector2d sub (double x, double y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	public Vector2d mul (@NotNull Vector2d v, @NotNull Vector2d dest) {
		return mul(v.x(), v.y(), dest);
	}

	public Vector2d mul (double x, double y, @NotNull Vector2d dest) {
		dest.x(this.x * x);
		dest.y(this.y * y);
		return dest;
	}

	@Contract("_ -> this")
	public Vector2d mul (@NotNull Vector2d v) {
		return mul(v.x(), v.y());
	}

	@Contract("_ -> this")
	public Vector2d mul (double d) {
		return mul(d, d);
	}

	@Contract("_,_ -> this")
	public Vector2d mul (double x, double y) {
		this.x *= x;
		this.y *= y;
		return this;
	}

	public Vector2d div (@NotNull Vector2d v, @NotNull Vector2d dest) {
		return div(v.x(), v.y(), dest);
	}

	public Vector2d div (double x, double y, @NotNull Vector2d dest) {
		dest.x(this.x / x);
		dest.y(this.y / y);
		return dest;
	}

	@Contract("_ -> this")
	public Vector2d div (@NotNull Vector2d v) {
		return div(v.x(), v.y());
	}

	@Contract("_,_ -> this")
	public Vector2d div (double x, double y) {
		this.x /= x;
		this.y /= y;
		return this;
	}

	public Vector2d pow (@NotNull Vector2d v, @NotNull Vector2d dest) {
		return pow(v.x(), v.y(), dest);
	}

	public Vector2d pow (double d, @NotNull Vector2d dest) {
		return pow(d, d, dest);
	}

	public Vector2d pow (double x, double y, @NotNull Vector2d dest) {
		dest.x(Math.pow(this.x, x));
		dest.y(Math.pow(this.y, y));
		return dest;
	}

	@Contract("_ -> this")
	public Vector2d pow (@NotNull Vector2d v) {
		return pow(v.x(), v.y());
	}

	@Contract("_ -> this")
	public Vector2d pow (double d) {
		return pow(d, d);
	}

	@Contract("_,_ -> this")
	public Vector2d pow (double x, double y) {
		this.x = Math.pow(this.x, x);
		this.y = Math.pow(this.y, y);
		return this;
	}

	public double length () {
		return Math.sqrt(x * x + y * y);
	}

	public double lengthSquared () {
		return x * x + y * y;
	}

	public double distance (@NotNull Vector2d v) {
		return distance(v.x(), v.y());
	}

	public double distance (double x, double y) {
		double dx = this.x - x;
		double dy = this.y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public double distanceSquared (double x, double y) {
		double dx = this.x - x;
		double dy = this.y - y;
		return dx * dx + dy * dy;
	}

	@Contract("-> this")
	public Vector2d normalize () {
		double len = length();
		x /= len;
		y /= len;
		return this;
	}

	@Contract("_ -> this")
	public Vector2d normalize (double length) {
		double len = length() / length;
		x /= len;
		y /= len;
		return this;
	}

	@Contract("-> this")
	public Vector2d normalizeSafely () {
		double len = length();
		if (len == 0) {
			throw new IllegalStateException("Vector length is 0");
		}
		x /= len;
		y /= len;
		return this;
	}

	@Contract("_ -> this")
	public Vector2d normalizeSafely (double length) {
		double len = length() / length;
		if (len == 0) {
			throw new IllegalStateException("Vector length is 0");
		}
		x /= len;
		y /= len;
		return this;
	}

	@Contract("_ -> this")
	public Vector2d rotateTo (@NotNull Vector2d direction) {
		return direction.normalize(length());
	}

	@Contract("-> this")
	public Vector2d zero () {
		x = y = 0;
		return this;
	}

	@Contract("-> this")
	public Vector2d negate () {
		x = -x;
		y = -y;
		return this;
	}

	public double dot (@NotNull Vector2d v) {
		return x() * v.x() + y() * v.y();
	}

	@Contract("_,_ -> this")
	public Vector2d clamp (double min, double max) {
		x = Math.min(Math.max(x, min), max);
		y = Math.min(Math.max(y, min), max);
		return this;
	}

	@Contract("_,_ -> this")
	public Vector2d clamp (@NotNull Vector2d min, @NotNull Vector2d max) {
		x = Math.min(Math.max(x, min.x()), max.x());
		y = Math.min(Math.max(y, min.y()), max.y());
		return this;
	}

	@Contract("_,_ -> this")
	public Vector2d lerp (@NotNull Vector2d end, double t) {
		x = x + (end.x() - x) * t;
		y = y + (end.y() - y) * t;
		return this;
	}

	@Contract("_,_ -> this")
	public Vector2d lerp (@NotNull Vector2d end, @NotNull Vector2d t) {
		x = x + (end.x() - x) * t.x();
		y = y + (end.y() - y) * t.y();
		return this;
	}

	@Contract("-> this")
	public Vector2d absolute () {
		x = Math.abs(x);
		y = Math.abs(y);
		return this;
	}

	@Contract("-> new")
	public Vector2d copy () {
		return Vector2d.of(x, y);
	}

	public boolean isFinite () {
		return Double.isFinite(x) && Double.isFinite(y);
	}
}
